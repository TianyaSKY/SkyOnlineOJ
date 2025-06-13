document.addEventListener('DOMContentLoaded', function () {
    const urlParams = new URLSearchParams(location.search);
    const solutionId = urlParams.get("solutionId");

    if (!solutionId) {
        document.getElementById("solution-detail").innerHTML = '<p>无效的题解ID</p>';
        return;
    }

    // 请求题解详情
    fetch(`/api/getSolutionDetail?solutionId=${solutionId}`)
        .then(res => res.json())
        .then(solution => {
            if (!solution || !solution.problemId) {
                document.getElementById("solution-detail").innerHTML = '<p>题解内容为空</p>';
                return;
            }

            const contentHtml = `
                <div class="solution-header">
                    <h2>${solution.title || '未命名题解'}</h2>
                    <div class="solution-author">${solution.authorName} 的题解</div>
                </div>
                <div class="solution-body">
                    <p>${solution.content ? solution.content.replace(/\n/g, '<br>') : '暂无内容'}</p>
                </div>
            `;

            document.getElementById("solution-detail").innerHTML = contentHtml;
        })
        .catch(error => {
            console.error('获取题解详情失败:', error);
            document.getElementById("solution-detail").innerHTML = '<p>加载题解详情时发生错误</p>';
        });

    // 请求题目信息
    fetch(`/api/problems/${solutionId.split('_')[0] || '1'}`)
        .then(res => res.json())
        .then(problem => {
            const difficultyMap = {
                0: { text: '简单', class: 'easy' },
                1: { text: '中等', class: 'medium' },
                2: { text: '困难', class: 'hard' }
            };
            const difficulty = difficultyMap[parseInt(problem.Diff, 10)] || difficultyMap[0];

            const problemHtml = `
                <div class="problem-details">
                    <h2>${problem.title} <span class="difficulty-badge ${difficulty.class}">${difficulty.text}</span></h2>
                    <p>${problem.description}</p>
                    <p>输入格式：<br><code>${problem.InputFormat || '无特别说明'}</code></p>
                    <p>输出格式：<br><code>${problem.OutputFormat || '无特别说明'}</code></p>
                    <p>样例输入：<br><code>${problem.TestData}</code></p>
                    <p>样例输出：<br><code>${problem.OutputData}</code></p>
                    <p>提示：<br><small>${problem.Hint || '暂无提示'}</small></p>
                </div>
            `;

            document.getElementById("problem-info").innerHTML = problemHtml;
        })
        .catch(error => {
            console.error('获取题目信息失败:', error);
            document.getElementById("problem-info").innerHTML = '<p>加载题目信息失败</p>';
        });
});
