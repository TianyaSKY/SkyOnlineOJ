document.addEventListener('DOMContentLoaded', function () {
    const problemId = new URLSearchParams(location.search).get("problemId");

    // 获取当前用户信息
    fetch('/api/profile')
        .then(res => {
            if (!res.ok) {
                throw new Error('未登录或会话过期');
            }
            return res.json();
        })
        .then(user => {
            // 👇 显式判断用户是否存在
            if (!user || !user.User_ID) {
                throw new Error('用户未登录');
            }

            const userId = user.User_ID;

            document.getElementById('solution-form').addEventListener('submit', function (e) {
                e.preventDefault();
                const content = document.getElementById('solution-content').value;

                fetch('/api/addSolution', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        problemId,
                        userId,     // 使用真实用户 ID
                        content
                    })
                }).then(res => {
                    if (res.ok) {
                        alert('题解提交成功');
                        window.location.href = `solution.html?id=${problemId}`;
                    } else {
                        alert('提交失败，请重试');
                    }
                });
            });
        })
        .catch(error => {
            console.error('获取用户信息失败:', error);
            alert('请先登录');
            window.location.href = 'login.html'; // 如果未登录，跳转到登录页
        });
});
