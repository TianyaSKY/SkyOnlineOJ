-- 先删除有外键约束的表
DROP TABLE SUBMIT;
DROP TABLE EVALUATION;
DROP TABLE QUESTION_ANSWER;
DROP TABLE EVALUATION_POINT;
-- 最后删除没有外键约束或被引用的表
DROP TABLE QUESTION;
DROP TABLE USERS;