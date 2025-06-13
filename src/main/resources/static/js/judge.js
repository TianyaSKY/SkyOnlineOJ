// 获取题目ID
const urlParams = new URLSearchParams(window.location.search);
const problemId = urlParams.get('id');

// 获取当前用户ID（从登录信息中获取）
let user_id = null;

// 页面加载时先获取用户信息
fetch('/api/profile')
    .then(res => {
        if (!res.ok) throw new Error("未登录");
        return res.json();
    })
    .then(user => {
        user_id = user.User_ID;

        // 获取提交的代码（可以从 localStorage 或其他方式获取）
        const code = localStorage.getItem('submittedCode') || 'print("Hello World")';

        // 获取测试点数据
        fetch(`/api/getTestCases?problemId=${problemId}`)
            .then(res => {
                if (!res.ok) throw new Error("获取测试点失败");
                return res.json();
            })
            .then(testCases => {
                let passedCount = 0;
                let total = testCases.length;

                // 异步处理所有测试点
                const statusPromises = testCases.map((testCase, index) =>
                    determineTestCaseStatus(problemId, user_id, code, index + 1)
                );

                Promise.all(statusPromises).then(statuses => {
                    const testCasesContainer = document.getElementById('test-cases');
                    const summaryContainer = document.getElementById('summary');
                    const boxContainer = document.getElementById('test-case-boxes');

                    statuses.forEach((status, index) => {
                        const box = document.createElement('div');
                        box.className = `test-box ${status.toLowerCase()}`;
                        box.title = `测试点 #${index + 1}：${status}`;
                        box.innerText = index + 1;
                        boxContainer.appendChild(box);

                        if (status === 'AC') passedCount++;
                    });


                    // 显示总结信息
                    summaryContainer.innerHTML = `
                        <h3>评测总结</h3>
                        <div class="summary-item">通过测试点：${passedCount}/${total}</div>
                        <div class="summary-item">成功率：${Math.round((passedCount / total) * 100)}%</div>
                        <div class="summary-item">状态：${passedCount === total ? 'Accepted' : '部分通过或未通过'}</div>
                    `;
                    // 判断是否全部通过
                    if (passedCount === total) {
                        fetch('/api/updateAllThrough', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded'
                            },
                            body: `user_id=${user_id}&Problem_id=${problemId}`
                        })
                            .then(res => {
                                if (!res.ok) {
                                    console.error("更新通过状态失败");
                                }
                            });
                    }

                });
            })
            .catch(err => {
                console.error('Error fetching test cases:', err);
                document.getElementById('test-cases').innerHTML = '<p>无法加载测试点</p>';
            });
    })
    .catch(err => {
        alert("请先登录");
        window.location.href = '/login.html';
    });

// 调用接口判断每个测试点是否通过
async function determineTestCaseStatus(problemId, user_id, code, testCaseNumber) {
    try {
        const response = await fetch(`/api/judgeTestCase?problemId=${problemId}&user_id=${user_id}&case_id=${testCaseNumber}&code=${encodeURIComponent(code)}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const result = await response.json();
        return result.correct ?  'AC' : 'WA';
    } catch (err) {
        console.error('评测测试点失败:', err);
        return 'ERROR';
    }
}
