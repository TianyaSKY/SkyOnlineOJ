-- 用户表
CREATE TABLE USERS(
                      USER_ID INT PRIMARY KEY ,
                      USER_NAME VARCHAR(30) NOT NULL ,
                      IS_ADMIN NUMBER(1) ,
                      CHECK ( IS_ADMIN IN (0,1) )
);
-- 题目表
CREATE TABLE QUESTION(
                         QUE_ID INT PRIMARY KEY,
                         QUE_TITLE VARCHAR2(100) NOT NULL,
                         QUE_DIFF VARCHAR2(4) NOT NULL,
                         QUE_CONTENT CLOB NOT NULL,
                         QUE_TESTI CLOB,          -- 样例输入
                         QUE_TESTO CLOB,          -- 样例输出
                         INPUTFORMAT CLOB,        -- 输入格式说明
                         OUTPUTFORMAT CLOB,       -- 输出格式说明
                         HINT CLOB                -- 提示信息
);

-- 题解表
CREATE TABLE QUESTION_ANSWER(
                                QUESTION_ANS_ID INT PRIMARY KEY ,
                                QUE_ID INT,
                                USER_ID INT,
                                QUESTION_ANS_CONTENT CLOB,
                                FOREIGN KEY (QUE_ID) REFERENCES QUESTION(QUE_ID),
                                FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID)
);
-- 评测点
CREATE TABLE EVALUATION_POINT(
                                 EVALUATE_POINT_ID INT,
                                 QUE_ID INT,
                                 INPUT_DATA CLOB,
                                 OUTPUT_DATA CLOB,
                                 PRIMARY KEY (EVALUATE_POINT_ID, QUE_ID),
                                 FOREIGN KEY (QUE_ID) REFERENCES QUESTION(QUE_ID)
);
-- 评测表
CREATE TABLE EVALUATION(
                           EVALUATE_ID INT PRIMARY KEY ,
                           EVALUATE_CONDITION VARCHAR(80),
                           USER_ID INT,
                           QUE_ID INT,
                           EVALUATE_POINT_ID INT,
                           FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID),
                           FOREIGN KEY (EVALUATE_POINT_ID,QUE_ID) REFERENCES EVALUATION_POINT(EVALUATE_POINT_ID,QUE_ID)
);
-- 提交记录
CREATE TABLE SUBMIT(
                       SUBMIT_ID INT PRIMARY KEY ,  -- 提交记录ID
                       QUE_ID INT,  -- 题目ID
                       USER_ID INT, -- 用户ID
                       RUN_TIME FLOAT,  -- 运行时间
                       RUN_MEM FLOAT,  -- 运行内存
                        CODE_LANG VARCHAR2(10),  -- 编程语言
                        CODE_CONTENT CLOB,  -- 代码内容
                        SUBMIT_TIME DATE,   -- 提交时间
                       ALL_THROUGH NUMBER(1),  -- 全部通过
                       FOREIGN KEY (QUE_ID) REFERENCES QUESTION(QUE_ID),
                       FOREIGN KEY (USER_ID) REFERENCES USERS(USER_ID)
)