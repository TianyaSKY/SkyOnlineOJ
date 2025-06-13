
// 加载题目内容
const id = new URLSearchParams(location.search).get("id");

fetch(`/api/problems/${id}`)
    .then(res => res.json())
    .then(p => {

        const difficultyMap = {
            0: { text: '简单', class: 'easy' },
            1: { text: '中等', class: 'medium' },
            2: { text: '困难', class: 'hard' }
        };
        const difficulty = difficultyMap[parseInt(p.Diff, 10)] || difficultyMap[0];
        document.getElementById("problem-content").innerHTML = `
        <h2>${p.title} <span class="difficulty-badge ${difficulty.class}">${difficulty.text}</span></h2>
        <p>${p.description}</p>
        
        <h3>输入格式</h3>
        <div class="sample-data">${p.InputFormat || '无特别说明'}</div>
        
        <h3>输出格式</h3>
        <div class="sample-data">${p.OutputFormat || '无特别说明'}</div>
        
        <h3>样例输入</h3>
        <div class="sample-data">${p.TestData.replace(/\\n/g, '<br>')}</div>
        
        <h3>样例输出</h3>
        <div class="sample-data">${p.OutputData.replace(/\\n/g, '<br>')}</div>

        
        <h3>提示</h3>
        <p>${p.Hint || '无特别提示'}</p>
      `;
    })
    .catch(err => {
        document.getElementById("problem-content").innerHTML = `
        <h2>加载题目失败</h2>
        <p>${err.message}</p>
      `;
    });

// 初始化代码编辑器
const editor = CodeMirror.fromTextArea(document.getElementById('editor'), {
    mode: 'python',
    lineNumbers: true,
    theme: 'dracula',
    indentUnit: 4,
    tabSize: 4,
    lineWrapping: true,
    autoCloseBrackets: true,
    matchBrackets: true,
    extraKeys: {
        'Ctrl-Space': 'autocomplete'
    }
});


// 提交代码
function submitCode() {

    const code = editor.getValue();
    if (!code.trim()) {
        alert('请先编写代码再提交！');
        return;
    }
    localStorage.setItem('submittedCode', code);
    // 获取身份信息
    loadUserInfoAndRun(user => {

        fetch("/api/submit", {
            method: "POST",
            headers: {"Content-Type": "application/x-www-form-urlencoded"},
            body: `problemId=${id}&code=${encodeURIComponent(code)}&user_id=${user.User_ID}`
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('提交失败');
                }
                return response.json();
            })
            .then(() => {
                window.location.href = `judge.html?id=${id}`;
            })
            .catch(err => {
                alert(`提交错误: ${err.message}`);
            });
    })
}
function loadUserInfoAndRun(callback) {
    fetch('/api/profile')
        .then(response => {
            if (!response.ok) throw new Error('未登录');
            return response.json();
        })
        .then(user => {
            callback(user); // 在这里调用传入的函数并传递 user
        })
        .catch(error => {
            document.location.href = '/login.html';
        });
}


