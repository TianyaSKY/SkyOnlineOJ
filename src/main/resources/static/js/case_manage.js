// 检查权限并加载数据
import { checkAdminPermission } from './checkAdminPermission.js';

document.addEventListener('DOMContentLoaded', function () {
    checkAdminPermission()
        .catch(error => {
            console.error('Error:', error);
            alert("请先登录");
            window.location.href = 'login.html';
        });
    // 获取 DOM 元素
    const modal = document.getElementById('add-testcase-modal');
    const openModalBtn = document.getElementById('add-testcase-btn');
    const closeModalBtn = document.querySelector('.modal .close-btn');
    const form = document.getElementById('add-testcase-form');
    const tbody = document.getElementById('testcases-list');

    // 页面加载时加载评测点数据（可选）
    const urlParams = new URLSearchParams(window.location.search);
    loadTestCases()

    // 打开模态框
    openModalBtn.addEventListener('click', () => {
        modal.style.display = 'flex';
    });

    // 关闭模态框
    closeModalBtn.addEventListener('click', () => {
        modal.style.display = 'none';
        form.reset(); // 重置表单
    });

    // 表单提交
    form.addEventListener('submit', function (e) {
        e.preventDefault();

        const problemId = document.getElementById('testcase-problem-id').value.trim();
        const testcaseNumber = document.getElementById('testcase-number').value.trim();
        const input = document.getElementById('testcase-input').value.trim() || "无";
        const output = document.getElementById('testcase-output').value.trim();

        if (!problemId || !testcaseNumber || !input || !output) {
            alert('所有字段都是必填项，请检查输入内容！');
            return;
        }

        // 构造请求体
        const testCaseData = {
            problemId: problemId,
            testcaseNumber: testcaseNumber,
            input: input,
            output: output
        };

        // 发送 POST 请求到后端
        fetch('/api/addTestCase', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(testCaseData)
        })
        .then(response => {
            if (response.ok) {
                return response.json(); // 假设返回新评测点数据
            } else {
                throw new Error('提交失败');
            }
        })
        .then(data => {
            alert('评测点提交成功');
            modal.style.display = 'none';
            form.reset();
            addRowToTable(data); // 将新评测点插入表格
        })
        .catch(error => {
            console.error('Error:', error);
            alert('提交评测点失败: ' + error.message);
        });
    });

    // 加载评测点列表
    function loadTestCases() {
        fetch(`/api/getAllTestCases`)
            .then(res => {
                if (!res.ok) throw new Error('加载失败');
                return res.json();
            })
            .then(testCases => {
                testCases.forEach(tc => {
                    addRowToTable(tc);
                });
            })
            .catch(err => {
                console.error('加载评测点失败:', err);
                alert('加载评测点失败');
            });
    }

    // 动态添加一行到表格
    function addRowToTable(data) {
        const newRow = document.createElement('tr');
        newRow.innerHTML = `
        <td>${data.problemId}</td>
        <td>${data.testcaseNumber}</td>
        <td><pre class="truncate-text">${data.input}</pre></td>
        <td><pre class="truncate-text">${data.output}</pre></td>
        <td>
            <button class="btn btn-danger btn-delete">删除</button>
        </td>
    `;


        newRow.querySelector('.btn-delete').addEventListener('click', () => {
            if (confirm('确定要删除这个评测点吗？')) {
                deleteTestCase(data.id || data.testcaseNumber, newRow);
            }
        });

        tbody.appendChild(newRow);
    }

    // 删除评测点
    function deleteTestCase(id, row) {
        fetch('/api/deleteTestCase', {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ id })
        })
        .then(res => {
            if (res.ok) {
                tbody.removeChild(row);
                alert('评测点删除成功');
            } else {
                throw new Error('删除失败');
            }
        })
        .catch(err => {
            console.error('删除失败:', err);
            alert('删除评测点失败');
        });
    }
});
