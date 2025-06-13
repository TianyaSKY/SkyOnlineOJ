document.addEventListener('DOMContentLoaded', function() {
    // 获取用户信息
    fetch('/api/profile')
        .then(response => {
            if (!response.ok) {
                throw new Error('未登录');
            }
            return response.json();
        })
        .then(user => {
            // 显示用户信息
            const userContainer = document.getElementById('user-container');
            userContainer.innerHTML = `
                    <div class="user-info">
                        <div class="user-avatar">${user.UserName.charAt(0).toUpperCase()}</div>
                        <div>
                            <h3>${user.UserName}</h3>
                            <p>ID: ${user.User_ID}</p>
                        </div>
                    </div>
                `;

            // 更新统计数据
            document.getElementById('solved-count').textContent = user.SuccessCount || 0;
            document.getElementById('submission-count').textContent = user.SubmitCount || 0;
        })
        .catch(error => {
            // 未登录状态
            const userContainer = document.getElementById('user-container');
            userContainer.innerHTML = `
                    <div class="login-prompt">
                        <p>您尚未登录，<a href="/login.html">请登录</a>以获取完整功能</p>
                    </div>
                    <div class="user-info">
                        <div class="user-avatar">?</div>
                        <div>
                            <h3>访客</h3>
                            <p>登录后可查看个人信息</p>
                        </div>
                    </div>
                `;
        });

    // 可以添加其他页面初始化逻辑
});