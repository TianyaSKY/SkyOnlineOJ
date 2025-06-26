let currentPage = 1;
const itemsPerPage = 10;
let allSubmissions = []; // 存储所有提交记录
let filteredSubmissions = [];

// 模拟获取提交记录
function fetchSubmissions() {
    fetch('/api/my-submissions')
        .then(res => res.json())
        .then(data => {
            allSubmissions = data;
            filteredSubmissions = [...allSubmissions];
            renderTable();
        })
        .catch(err => {
            console.error('获取提交记录失败:', err);
            document.getElementById('submissionList').innerHTML = '<p>无法加载提交记录。</p>';
        });
}

// 渲染提交记录
function renderTable() {
    const container = document.getElementById('submissionList');
    container.innerHTML = '';

    const start = (currentPage - 1) * itemsPerPage;
    const end = start + itemsPerPage;
    const pageItems = filteredSubmissions.slice(start, end);

    if (pageItems.length === 0) {
        container.innerHTML = '<p>暂无提交记录。</p>';
        return;
    }

    const table = document.createElement('table');
    table.innerHTML = `
        <thead>
            <tr>
                <th>题目ID</th>
                <th>题目名称</th>
                <th>状态</th>
                <th>语言</th>
                <th>运行时间</th>
                <th>内存占用</th>
                <th>提交时间</th>
            </tr>
        </thead>
        <tbody>
            ${pageItems.map(sub => `
                <tr class="${getStatusClass(sub.status)}">
                    <td>${sub.problemId}</td>
                    <td><a href="problem_detail.html?id=${sub.problemId}">${sub.title}</a></td>
                    <td>${translateStatus(sub.status)}</td>
                    <td>${sub.language}</td>
                    <td>${sub.runTime ? `${sub.runTime} ms` : '-'}</td>
                    <td>${sub.memoryUsed ? `${sub.memoryUsed} KB` : '-'}</td>
                    <td>${new Date(sub.submitTime).toLocaleString()}</td>
                </tr>
            `).join('')}
        </tbody>
    `;

    container.appendChild(table);
    updatePagination();
}

// 状态翻译
function translateStatus(status) {
    const map = {
        accepted: '通过',
        'wrong-answer': '错误答案',
        running: '运行中',
        'compile-error': '编译错误'
    };
    return map[status] || status;
}

// 获取状态类名
function getStatusClass(status) {
    return {
        accepted: 'status-success',
        'wrong-answer': 'status-danger',
        running: 'status-warning',
        'compile-error': 'status-info'
    }[status] || '';
}

// 筛选函数
function filterSubmissions() {
    const selectedStatus = document.getElementById('statusFilter').value;
    if (!selectedStatus) {
        filteredSubmissions = [...allSubmissions];
    } else {
        filteredSubmissions = allSubmissions.filter(s => s.status === selectedStatus);
    }
    currentPage = 1;
    renderTable();
}

// 分页控制
function updatePagination() {
    const totalPages = Math.ceil(filteredSubmissions.length / itemsPerPage);
    document.getElementById('pageInfo').innerText = `第 ${currentPage} 页 / 共 ${totalPages || 1} 页`;
    document.getElementById('prevBtn').disabled = currentPage === 1;
    document.getElementById('nextBtn').disabled = currentPage === totalPages || totalPages === 0;
}

function prevPage() {
    if (currentPage > 1) {
        currentPage--;
        renderTable();
    }
}

function nextPage() {
    const totalPages = Math.ceil(filteredSubmissions.length / itemsPerPage);
    if (currentPage < totalPages) {
        currentPage++;
        renderTable();
    }
}

// 初始化
window.onload = fetchSubmissions;

// 绑定事件监听器
document.getElementById('statusFilter').addEventListener('change', filterSubmissions);
