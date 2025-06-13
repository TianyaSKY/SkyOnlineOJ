import { checkAdminPermission } from './checkAdminPermission.js';


function deleteUser(userId) {
    if (confirm(`确定要删除用户 ${userId} 吗？`)) {
        console.log('删除用户:', userId);
        // 实现删除逻辑
        // 调用添加用户API
        fetch("/api/deleteUser", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `user_id=${encodeURIComponent(userId)}`
        })
            .then(response => {
                if (!response.ok) throw new Error('删除用户失败');
                return response.json();
            })
            .then(data => {
                if (data) {
                    resetAddUserForm();
                    // 刷新用户列表
                    fetchUserData();
                } else {
                    throw new Error('添加用户失败');
                }
            })
            .catch(error => {
                errorMsg.style.display = 'block';
                errorMsg.textContent = `错误: ${error.message}`;
                console.error('删除用户失败:', error);
            });
        alert(`用户 ${userId} 已删除`);
    }
}
function editUser(userId) {
    // 1. 显示模态框
    const modal = document.getElementById('edit-user-modal');
    modal.style.display = 'flex';
    document.getElementById('edit-user-modal').classList.add('show');


    fetch(`/api/queryUser/${userId}`, {})
        .then(response => response.json())
        .then(user => {
            // 填充表单数据
            document.getElementById('edit-username').value = user.UserName || user.username;
            document.getElementById('edit-user-id').value = userId;

            // 设置权限选项
            const isAdmin = user.admin;
            document.querySelector(`input[name="edit-admin"][value="${isAdmin}"]`).checked = true;
        })
        .catch(error => {
            console.error('获取用户数据失败:', error);
            alert('获取用户信息失败');
        });

    // 3. 关闭按钮事件
    document.querySelector('.close-btn').onclick = () => {
        modal.style.display = 'none';
    };

    // 4. 点击模态框外部关闭
    modal.onclick = (e) => {
        if (e.target === modal) {
            modal.style.display = 'none';
            document.getElementById('edit-user-modal').classList.remove('show');
        }
    };
}

// 表单提交处理
document.getElementById('edit-user-form').addEventListener('submit', function(e) {
    e.preventDefault();

    const userId = document.getElementById('edit-user-id').value;
    const username = document.getElementById('edit-username').value;
    const isAdmin = document.querySelector('input[name="edit-admin"]:checked').value === 'true';

    // 调用API更新用户
    fetch('/api/upDateUser', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
            user_id: userId,      // 和后端参数名一致
            new_name: username,   // 和后端参数名一致
            isAdmin: isAdmin      // 保持相同
        })
    })
        .then(response => {
            if (response.ok) {
                alert('用户信息更新成功');
                document.getElementById('edit-user-modal').style.display = 'none';
                fetchUserData(); // 刷新用户列表
            } else {
                throw new Error('更新失败');
            }
        })
        .catch(error => {
            console.error('更新失败:', error);
            alert('更新用户信息失败');
        });
});

// 添加用户功能
document.addEventListener('DOMContentLoaded', () => {
    const addUserBtn = document.getElementById('add-user-btn');
    const addUserForm = document.getElementById('add-user-form');
    const submitUserBtn = document.getElementById('submit-user-btn');
    const cancelUserBtn = document.getElementById('cancel-user-btn');
    const newUsernameInput = document.getElementById('new-username');

    // 显示/隐藏添加用户表单
    addUserBtn.addEventListener('click', () => {
        addUserForm.style.display = 'flex';
        addUserBtn.style.display = 'none';
        newUsernameInput.focus();
    });

    // 取消添加用户
    cancelUserBtn.addEventListener('click', () => {
        resetAddUserForm();
    });

    // 提交新用户
    submitUserBtn.addEventListener('click', addNewUser);

    // 按Enter键提交
    newUsernameInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            addNewUser();
        }
    });
});

function resetAddUserForm() {
    document.getElementById('add-user-form').style.display = 'none';
    document.getElementById('add-user-btn').style.display = 'block';
    document.getElementById('new-username').value = '';
}

function addNewUser() {
    const username = document.getElementById('new-username').value.trim();
    const errorMsg = document.getElementById('error-message');

    if (!username) {
        errorMsg.style.display = 'block';
        errorMsg.textContent = '用户名不能为空';
        return;
    }

    // 调用添加用户API
    fetch("/api/addUser", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `user_name=${encodeURIComponent(username)}`
    })
        .then(response => {
            if (!response.ok) throw new Error('添加用户失败');
            return response.json();
        })
        .then(data => {
            if (data) {
                resetAddUserForm();
                // 刷新用户列表
                fetchUserData();
            } else {
                throw new Error('添加用户失败');
            }
        })
        .catch(error => {
            errorMsg.style.display = 'block';
            errorMsg.textContent = `错误: ${error.message}`;
            console.error('添加用户失败:', error);
        });
}
function fetchUserData() {
    checkAdminPermission()
        .catch(error => {
            console.error('Error:', error);
            alert("请先登录");
            window.location.href = 'login.html';
        });

    const loading = document.getElementById('loading');
    const errorMsg = document.getElementById('error-message');
    const tableBody = document.getElementById('users-table');

    loading.style.display = 'block';
    errorMsg.style.display = 'none';
    tableBody.innerHTML = '';

    // 调用后端API（注意路径和参数）
    fetch("/api/get_user_information", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json', // 改为JSON格式
        },
        // body: 不需要参数时可以省略
    })
        .then(response => {
            if (!response.ok) throw new Error(`HTTP错误! 状态码: ${response.status}`);
            return response.json();
        })
        .then(users => {
            loading.style.display = 'none';

            if (!users || users.length === 0) {
                tableBody.innerHTML = `
                <tr>
                    <td colspan="4" style="text-align: center;">暂无用户数据</td>
                </tr>
            `;
                return;
            }

            // 渲染用户数据（兼容不同字段名）
            tableBody.innerHTML = users.map(user => `
            <tr>
                <td>${user.User_ID || user.userId || 'N/A'}</td>
                <td>${user.UserName || user.username || 'N/A'}</td>
                <td>
                    <span class="${(user.IS_ADMIN || user.admin) ? 'admin-badge' : 'user-badge'}">
                        ${(user.IS_ADMIN || user.admin) ? '管理员' : '普通用户'}
                    </span>
                </td>
                <td>
                    <button class="action-btn edit-btn" data-user-id="${user.User_ID || user.userId}">
                        编辑
                    </button>
                    <button class="action-btn delete-btn" data-user-id="${user.User_ID || user.userId}">
                        删除
                    </button>

                </td>
            </tr>
        `).join('');
            addEventListenersToButtons(); //
        })
        .catch(error => {
            loading.style.display = 'none';
            errorMsg.style.display = 'block';
            errorMsg.textContent = `错误: ${error.message}`;
            console.error('获取用户数据失败:', error);

            // 调试用：打印完整错误信息
            if (error.response) {
                error.response.json().then(errData => {
                    console.error('错误详情:', errData);
                });
            }
        });
}
document.addEventListener('DOMContentLoaded', () => {
    console.log("页面加载完成，准备获取用户数据...");
    fetchUserData();
});

function addEventListenersToButtons() {
    // 编辑按钮
    document.querySelectorAll('.edit-btn').forEach(button => {
        button.addEventListener('click', () => {
            const userId = button.getAttribute('data-user-id');
            editUser(userId);
        });
    });

    // 删除按钮
    document.querySelectorAll('.delete-btn').forEach(button => {
        button.addEventListener('click', () => {
            const userId = button.getAttribute('data-user-id');
            deleteUser(userId);
        });
    });
}
