async function login() {
    // 获取用户输入
    const userId = document.getElementById('user_id').value.trim();
    const userName = document.getElementById('user_name').value.trim();

    // 简单验证输入是否为空
    if (!userId || !userName) {
        alert('用户ID和用户名不能为空');
        return;
    }

    try {
        // 发送登录请求
        const response = await fetch('/api/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `user_id=${encodeURIComponent(userId)}&user_name=${encodeURIComponent(userName)}`
        });

        if (response.ok) {
            const result = await response.json();
            if (result) {
                // 登录成功，跳转到首页
                window.location.href = 'index.html';
            } else {
                alert('登录失败，请检查用户ID和用户名是否正确');
            }
        } else {
            throw new Error('网络响应不正常');
        }
    } catch (error) {
        console.error('登录过程中出现错误:', error);
        alert('登录过程中出现错误，请稍后再试');
    }
}

// 添加回车键登录功能
document.addEventListener('DOMContentLoaded', function() {
    const inputs = document.querySelectorAll('#user_id, #user_name');
    inputs.forEach(input => {
        input.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                login();
            }
        });
    });
});