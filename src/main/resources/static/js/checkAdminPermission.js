// 检查管理员权限
export function checkAdminPermission() {
    return fetch("/api/profile")
        .then(res => {
            if (!res.ok) throw new Error('未登录');
            return res.json();
        })
        .then(user => {
            if (!user || !user.admin) {
                throw new Error('无权访问管理页面');
            }
            // 显示管理员内容
            document.querySelectorAll('.admin-only').forEach(el => {
                el.style.display = 'block';
            });
        });
}