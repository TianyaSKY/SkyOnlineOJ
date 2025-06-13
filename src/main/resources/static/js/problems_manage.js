// 检查权限并加载数据
import { checkAdminPermission } from './checkAdminPermission.js';



document.addEventListener('DOMContentLoaded', function() {
    checkAdminPermission()
        .catch(error => {
            console.error('Error:', error);
            alert("请先登录");
            window.location.href = 'login.html';
        });
    // 获取DOM元素
    const addProblemBtn = document.getElementById('add-problem-btn');
    const modal = document.getElementById('add-problem-modal');
    const closeBtn = document.querySelector('.close-btn');
    const addProblemForm = document.getElementById('add-problem-form');
    const problemsList = document.getElementById('problems-list');

    // 加载题目列表
    loadProblems();

    // 打开模态框
    addProblemBtn.addEventListener('click', function() {
        modal.style.display = 'flex';
    });

    // 关闭模态框
    closeBtn.addEventListener('click', function() {
        modal.style.display = 'none';
    });

    // 点击模态框外部关闭
    window.addEventListener('click', function(event) {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });

    // 提交表单
    addProblemForm.addEventListener('submit', function(e) {
        e.preventDefault();

        const problemData = {
            id: document.getElementById('problem-id').value,
            title: document.getElementById('problem-title').value,
            description: document.getElementById('problem-description').value,
            difficulty: document.getElementById('problem-difficulty').value,

            TestData: document.getElementById('sample-input').value || "无",
            OutputData: document.getElementById('sample-output').value,

            InputFormat: document.getElementById('input-format').value,
            OutputFormat: document.getElementById('output-format').value,
            Hint: document.getElementById('hint').value
        };

        // 发送到后端API
        addProblem(problemData);
    });

    function loadProblems() {
        fetch('/api/GetAllProblems')
            .then(response => {
                if (!response.ok) {
                    throw new Error('网络响应不正常');
                }
                return response.json();
            })
            .then(problems => {
                problemsList.innerHTML = '';
                problems.forEach(problem => {
                    // 将数字难度转换为文字和对应的CSS类
                    const difficultyMap = {
                        0: { text: '简单', class: 'easy' },
                        1: { text: '中等', class: 'medium' },
                        2: { text: '困难', class: 'hard' }
                    };
                    const difficulty = difficultyMap[parseInt(problem.Diff, 10)] || difficultyMap[0];

                    const row = document.createElement('tr');
                    row.innerHTML = `
                        <td>${problem.id}</td>
                        <td>${problem.title}</td>
                        <td><span class="difficulty-badge ${difficulty.class}">${difficulty.text}</span></td>
                        <td>
                            <button class="btn btn-primary view-btn" data-id="${problem.id}">查看</button>
                            <button class="btn btn-danger delete-btn" data-id="${problem.id}">删除</button>
                        </td>
                    `;
                    problemsList.appendChild(row);
                });
            })
            .catch(error => {
                console.error('加载题目失败:', error);
                alert('加载题目列表失败: ' + error.message);
            });
    }

    // 添加题目函数
    function addProblem(problemData) {
        fetch('/api/addProblem', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(problemData)
        })
            .then(response => {
                if (response.ok) {
                    alert('题目添加成功');
                    modal.style.display = 'none';
                    addProblemForm.reset();
                    loadProblems(); // 刷新列表
                } else {
                    return response.text().then(text => { throw new Error(text) });
                }
            })
            .catch(error => {
                console.error('添加题目失败:', error);
                alert('添加题目失败: ' + error.message);
            });
    }

    // 事件委托处理查看和删除按钮
    problemsList.addEventListener('click', function(e) {
        if (e.target.classList.contains('delete-btn')) {
            const problemId = e.target.getAttribute('data-id');
            if (confirm(`确定要删除题目 ${problemId} 吗？`)) {
                deleteProblem(problemId);
            }
        } else if (e.target.classList.contains('view-btn')) {
            const problemId = e.target.getAttribute('data-id');
            viewProblemDetails(problemId);
        }
    });

    function deleteProblem(problemId) {
        fetch('/api/deleteProblem', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `que_id=${problemId}`
        })
            .then(response => {
                if (response.ok) {
                    return response.json(); // 解析返回的JSON数据
                } else {
                    return response.text().then(text => { throw new Error(text) });
                }
            })
            .then(data => {
                if (data) { // 根据后端返回的boolean值判断是否成功
                    alert('题目删除成功');
                    loadProblems(); // 刷新列表
                } else {
                    throw new Error('删除失败');
                }
            })
            .catch(error => {
                console.error('删除题目失败:', error);
                alert('删除题目失败: ' + error.message);
            });
    }

    // 查看题目详情函数
    function viewProblemDetails(problemId) {
        location.href = `/problem.html?id=${problemId}`;
    }
});