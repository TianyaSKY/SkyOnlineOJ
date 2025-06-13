document.addEventListener('DOMContentLoaded', function() {
    const id = new URLSearchParams(location.search).get("id");

    // 获取题目信息
    fetch(`/api/problems/${id}`)
        .then(res => res.json())
        .then(problem => {
            const difficultyMap = {
                0: { text: '简单', class: 'easy' },
                1: { text: '中等', class: 'medium' },
                2: { text: '困难', class: 'hard' }
            };

            const difficulty = difficultyMap[parseInt(problem.Diff, 10)] || difficultyMap[0];

            document.getElementById("problem-content").innerHTML = `
                <div class="problem-details">
                    <h2>${problem.title} <span class="difficulty-badge ${difficulty.class}">${difficulty.text}</span></h2>
                    <p>${problem.description}</p>
                    <p>样例数据：</p>
                    <p>输入数据: ${problem.TestData}</p>
                    <p>输出数据: ${problem.OutputData}</p>
                </div>`;
        })
        .catch(error => {
            console.error('获取题目详情失败:', error);
            document.getElementById("problem-content").innerHTML = '<p>加载题目详情时发生错误</p>';
        });

    // 获取题解列表
    fetch(`/api/solutions?problemId=${id}`)
        .then(res => res.json())
        .then(solutions => {
            const solutionsList = document.getElementById('solutions-list');

            if (solutions.length === 0) {
                solutionsList.innerHTML = '<p>暂无题解</p>';
                return;
            }

            solutions.forEach(solution => {
                const solutionCard = document.createElement('div');
                solutionCard.className = 'solution-card';
                solutionCard.innerHTML = `
        <a href="solution_detail.html?solutionId=${solution.solutionId}" class="solution-author-link">
            <div class="solution-author">${solution.authorName}的题解</div>
        </a>
    `;
                solutionsList.appendChild(solutionCard);
            });

        })
        .catch(error => {
            console.error('获取题解列表失败:', error);
            document.getElementById("solutions-list").innerHTML = '<p>加载题解列表时发生错误</p>';
        });
    document.getElementById("create-solution-btn").addEventListener("click", function () {
        window.location.href = `edit_solution.html?problemId=${id}`;
    });
});
