# Docker 环境搭建指南

## 1. Docker 安装

### ubuntu安装 Docker
```shell
sudo apt update
sudo apt upgrade -y
# 下载并执行官方安装脚本
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo systemctl enable docker # 设置开机自启
```

## 2. Docker 镜像加速配置

### ubuntu配置镜像加速

```shell
sudo mkdir -p /etc/docker
sudo vim /etc/docker/daemon.json # 写入下面的json
```
```json
{
    "registry-mirrors": [
     "https://docker.m.daocloud.io",
     "https://docker.imgdb.de",
     "https://docker-0.unsee.tech",
     "https://docker.hlmirror.com",
     "https://docker.1ms.run",
     "https://func.ink",
     "https://lispy.org",
     "https://docker.xiaogenban1993.com"
    ]
}
```
```shell
sudo systemctl daemon-reload
sudo systemctl restart docker
```

## 3. 必要服务安装指南

### 3.1 Nacos 安装配置

1. 拉取 Nacos 镜像
```bash
docker pull nacos/nacos-server:latest
```

2. 创建 Nacos 配置目录
```bash
mkdir -p ./nacos/logs  # 日志目录
mkdir -p ./nacos/data  # 数据目录
```

3. 启动 Nacos 容器
```bash
docker run -d \
  --name nacos \
  --restart=always \
  -p 8848:8848 \
  -p 9848:9848 \
  -p 9849:9849 \
  -e MODE=standalone \
  -e PREFER_HOST_MODE=hostname \
  -v ./nacos/conf:/home/nacos/conf \
  -v ./nacos/logs:/home/nacos/logs \
  nacos/nacos-server:latest
```

4. 访问 Nacos 控制台
```
http://localhost:8848/nacos
默认账号密码：nacos/nacos
```

​	5.点右上角+创建配置文件，命名gateway-routes.json,文件类型选json

![image-20250710151127349](.\images\image-20250710151127349.png)

```json
[
    {
        "id": "exam-route",
        "order": 0,
        "uri": "lb://exam-service",
        "predicates": [
            {
                "name": "Path",
                "args": {
                    "_genkey_0": "/api/exams/**"
                }
            }
        ],
        "filters": [
            {
                "name": "StripPrefix",
                "args": {
                    "parts": 1
                }
            }
        ]
    },
    {
        "id": "ai-route",
        "order": 0,
        "uri": "lb://ai-service",
        "predicates": [
            {
                "name": "Path",
                "args": {
                    "_genkey_0": "/api/ai/**"
                }
            }
        ],
        "filters": [
            {
                "name": "StripPrefix",
                "args": {
                    "parts": 1
                }
            }
        ]
    },
    {
        "id": "homework-route",
        "order": 0,
        "uri": "lb://homework-service",
        "predicates": [
            {
                "name": "Path",
                "args": {
                    "_genkey_0": "/api/assignments/**"
                }
            }
        ],
        "filters": [
            {
                "name": "StripPrefix",
                "args": {
                    "parts": 1
                }
            }
        ]
    },
    {
        "id": "knowledge-route",
        "order": 0,
        "uri": "lb://knowledge-service",
        "predicates": [
            {
                "name": "Path",
                "args": {
                    "_genkey_0": "/api/knowledge/**"
                }
            }
        ],
        "filters": [
            {
                "name": "StripPrefix",
                "args": {
                    "parts": 1
                }
            }
        ]
    },
    {
        "id": "learnCenter-route",
        "order": 0,
        "uri": "lb://learn-service",
        "predicates": [
            {
                "name": "Path",
                "args": {
                    "_genkey_0": "/api/learn/analysis/**"
                }
            }
        ],
        "filters": [
            {
                "name": "StripPrefix",
                "args": {
                    "parts": 1
                }
            }
        ]
    },
    {
        "id": "user-route",
        "order": 0,
        "uri": "lb://user-service",
        "predicates": [
            {
                "name": "Path",
                "args": {
                    "_genkey_0": "/api/users/**"
                }
            }
        ],
        "filters": [
            {
                "name": "StripPrefix",
                "args": {
                    "parts": 1
                }
            }
        ]
    },
    {
        "id": "back-service-route",
        "order": 0,
        "uri": "lb://back-service",
        "predicates": [
            {
                "name": "Path",
                "args": {
                    "_genkey_0": "/messages/**",
                    "_genkey_1": "/api/admin/**",
                    "_genkey_2": "/conversations/**",
                    "_genkey_3": "/api/courses/**",
                    "_genkey_4": "/api/evaluation/**",
                    "_genkey_5": "/api/files/**",
                    "_genkey_6": "/api/notice/**",
                    "_genkey_7": "/api/auth/**",
                    "_genkey_8": "/api/users/**"
                }
            }
        ]
    }
]
```



### 3.2 Elasticsearch 及 IK 分词器安装配置

1. 增加虚拟内存限制
```bash
# 永久修改（推荐）
echo "vm.max_map_count=262144" | sudo tee -a /etc/sysctl.conf
sudo sysctl -p  # 应用配置
```
   2.增加文件描述符限制

```
# 编辑 limits.conf
sudo vim /etc/security/limits.conf
# 添加以下内容（替换 your_username 为你的用户名）
your_username hard nofile 65536
your_username soft nofile 65536

# 保存并退出，然后重启系统使配置生效
```
3.拉取 Elasticsearch 镜像

```bash
docker pull elasticsearch:7.12.1
```

4.创建 Elasticsearch 配置目录

```bash
mkdir -p ./elasticsearch/data
mkdir -p ./elasticsearch/plugins
# 修改目录权限
chmod -R 777 ./elasticsearch
```

5.安装elasticsearch

```bash
docker run -d \
  --name elasticsearch \
  --restart=always \
  -p 9200:9200 \
  -p 9300:9300 \
  -e discovery.type=single-node \
  -e xpack.security.enabled=false \ 
  -e ES_JAVA_OPTS="-Xms512m -Xmx512m" \
  -v /mydata/elasticsearch/data:/usr/share/elasticsearch/data \
  -v /mydata/elasticsearch/plugins:/usr/share/elasticsearch/plugins \ 
  --ulimit memlock=-1:-1 \ 
  --ulimit nofile=65536:65536 \
  elasticsearch:7.12.1
```

  6.验证安装

```bash
curl http://localhost:9200
```
预期返回：

```json
 {
   "name" : "37aaf4d1e909",
  "cluster_name" : "docker-cluster",
  "cluster_uuid" : "VPNbR1B0RxiVO2OfM27CiQ",
  "version" : {
    "number" : "7.12.1",
    "build_flavor" : "default",
    "build_type" : "docker",
    "build_hash" : "3186837139b9c6b6d23c3200870651f10d3343b7",
    "build_date" : "2021-04-20T20:56:39.040728659Z",
    "build_snapshot" : false,
    "lucene_version" : "8.8.0",
    "minimum_wire_compatibility_version" : "6.8.0",
    "minimum_index_compatibility_version" : "6.0.0-beta1"
  },
  "tagline" : "You Know, for Search"
}
```

  7.安装ik分词器

```bash
# 进入容器
docker exec -it elasticsearch bash

# 安装 IK 分词器（版本需与 Elasticsearch 版本匹配）
bin/elasticsearch-plugin install https://release.infinilabs.com/analysis-ik/stable/elasticsearch-analysis-ik-7.12.1.zip

# 安装完成后，退出容器
exit
```
### 3.3 MongoDB 安装配置

1. 拉取 MongoDB 镜像
```bash
docker pull mongo:latest
```

2. 创建 MongoDB 数据目录
```bash
mkdir -p ./mongodb/data
```

3. 启动 MongoDB 容器
```bash
docker run -d \
  --name mongodb \
  -p 27017:27017 \
  -v ./mongodb/data:/data/db \
  -e MONGO_INITDB_ROOT_USERNAME=admin \
  -e MONGO_INITDB_ROOT_PASSWORD=admin \
  mongo:latest
```

4. 验证安装
```bash
docker exec -it mongodb mongosh -u admin -p admin
```

## 4 服务启动

### 环境变量修改
在每个服务的resources目录下都有一个.env文件，这是环境变量文件，修改其中的每个服务的ip和端口
### 启动

点击左下角的服务

![联想截图_20250706193813](.\images\联想截图_20250706193813.png)

点击左上角的加号--->运行配置--->选择springboot
![image-20250706193912646](.\images\image-20250706193912646.png)

填写名称、选择模块、选择主类（启动类）、将环境变量选择为对应模块下的.env文件（如果没有显示环境变量这个选项，就点击修改选项，然后把环境变量勾上）、然后点击应用

![image-20250706194202311](.\images\image-20250706194202311.png)

对于ai-service模块，需要使用api-key，需要到阿里云百炼平台获取[大模型服务平台百炼控制台](https://bailian.console.aliyun.com/?tab=model#/model-market)，获取完将api-key写入ai-service模块下的.env文件对应位置

## 5 数据初始化

连接到mongodb，执行下面的语句

```mongo
use exams
db.test_papers.drop()

db.test_papers.insertMany([{
    "_id": ObjectId("649a1c85f3f2b4c1e6d7b311"),
    "title": "高中数学必修一函数定义域与值域测试",
    "description": "",
    "courseId": 123456,
    "teacherId": 123456,
    "questions": [
        {
            "questionId": "Q0101",
            "content": "函数f(x)=√(x+3)+1/(x-2)的定义域是？",
            "type": "单选题",
            "options": [{
                "optionId": "A",
                "content": "x≥-3"
            }, {
                "optionId": "B",
                "content": "x≥-3且x≠2"
            }, {
                "optionId": "C",
                "content": "x≥-3且x≠2且x≠-3"
            }, {
                "optionId": "D",
                "content": "x≥-3且x≠2且x≠2"
            }],
            "score": 10,
            "answer": "B",
            "analysis": "根号内非负：x+3≥0 → x≥-3；分母不为0：x-2≠0 → x≠2，取交集。",
            "difficulty": "基础",
            "tags": ["函数定义域", "分式函数", "根式函数"]
        },
        {
            "questionId": "Q0102",
            "content": "已知函数f(x)=x²-4x+5，x∈[1,4]，其值域为？",
            "type": "单选题",
            "options": [{
                "optionId": "A",
                "content": "[1,5]"
            }, {
                "optionId": "B",
                "content": "[1,4]"
            }, {
                "optionId": "C",
                "content": "[2,5]"
            }, {
                "optionId": "D",
                "content": "[2,4]"
            }],
            "score": 10,
            "answer": "A",
            "analysis": "对称轴x=2，f(2)=1（最小值），f(1)=2，f(4)=5（最大值），值域[1,5]。",
            "difficulty": "中等",
            "tags": ["二次函数值域", "区间最值"]
        },
        {
            "questionId": "Q0103",
            "content": "函数f(x)=1/(x²+1)的值域是？",
            "type": "填空题",
            "options": [],
            "score": 10,
            "answer": "(0,1]",
            "analysis": "x²≥0 → x²+1≥1 → 0<1/(x²+1)≤1，值域(0,1]。",
            "difficulty": "基础",
            "tags": ["分式函数值域", "二次函数性质"]
        },
        {
            "questionId": "Q0105",
            "content": "证明函数f(x)=x+1/x在(1,+∞)上的单调性。",
            "type": "解答题",
            "options": [],
            "score": 10,
            "answer": "单调递增",
            "analysis": "任取x₁>x₂>1，f(x₁)-f(x₂)=(x₁-x₂)+(1/x₁-1/x₂)=(x₁-x₂)(1-1/(x₁x₂))>0，故单调递增。",
            "difficulty": "中等",
            "tags": ["单调性证明", "作差法"]
        },
    ],
    "createTime": new Date("2025-06-26T10:22:35Z"),
    "startTime": new Date("2025-07-26T10:22:35Z"),
    "status": "已发布",
    "duration": 60,
    "totalScore": 40,
},
    {
        "_id": ObjectId("649a1c85f3f2b4c1e6d7b312"),
        "title": "高中数学必修一函数单调性与奇偶性测试",
        "description": "",
        "courseId": 123456,
        "teacherId": 123456,
        "questions": [
            {
                "questionId": "Q0201",
                "content": "下列函数中，既是奇函数又是增函数的是？",
                "type": "多选题",
                "options": [
                    {
                        "optionId": "A",
                        "content": "f(x)=x³"
                    },
                    {
                        "optionId": "B",
                        "content": "f(x)=x²-1"
                    },
                    {
                        "optionId": "C",
                        "content": "f(x)=x²+1"
                    },
                    {
                        "optionId": "D",
                        "content": "f(x)=x²-2x"
                    }
                ],
                "score": 10,
                "answer": "A",
                "analysis": "f(x)=x³是奇函数，且在R上单调递增；B是减函数，C是偶函数，D在定义域内非单调。",
                "difficulty": "基础",
                "tags": ["函数奇偶性", "函数单调性"]
            },
            {
                "questionId": "Q0202",
                "content": "证明函数f(x)=x+1/x在(1,+∞)上的单调性。",
                "type": "解答题",
                "options": [],
                "score": 10,
                "answer": "单调递增",
                "analysis": "任取x₁>x₂>1，f(x₁)-f(x₂)=(x₁-x₂)+(1/x₁-1/x₂)=(x₁-x₂)(1-1/(x₁x₂))>0，故单调递增。",
                "difficulty": "中等",
                "tags": ["单调性证明", "作差法"]
            },
            {
                "questionId": "Q0203",
                "content": "已知f(x)是定义在R上的奇函数，当x>0时，f(x)=x²-2x，则x<0时f(x)=？",
                "type": "填空题",
                "options": [],
                "score": 10,
                "answer": "-x²-2x",
                "analysis": "设x<0，则-x>0，f(-x)=(-x)²-2(-x)=x²+2x，由奇函数f(x)=-f(-x)=-x²-2x。",
                "difficulty": "中等",
                "tags": ["奇函数性质", "分段函数"]
            }
        ],
        "createTime": new Date("2025-06-26T10:22:35Z"),
        "startTime": new Date("2025-07-26T10:22:35Z"),
        "status": "已发布",
        "duration": 60,
        "totalScore": 30
    },
    {
        "_id": ObjectId("649a1c85f3f2b4c1e6d7b313"),
        "title": "高中数学必修一指数与对数函数综合测试",
        "description": "",
        "courseId": 123456,
        "teacherId": 123456,
        "questions": [
            {
                "questionId": "Q0301",
                "content": "函数y=2^(x+1)的图像可以由y=2^x的图像如何变换得到？",
                "type": "单选题",
                "options": [{
                    "optionId": "A",
                    "content": "向左平移1个单位"
                }, {
                    "optionId": "B",
                    "content": "向右平移1个单位"
                }, {
                    "optionId": "C",
                    "content": "向左平移2个单位"
                }, {
                    "optionId": "D",
                    "content": "向右平移2个单位"
                }],
                "score": 10,
                "answer": "A",
                "analysis": "y=2^(x+1)=2^[(x)+1]，根据平移规律，左加右减，故向左平移1个单位。",
                "difficulty": "基础",
                "tags": ["指数函数图像", "函数平移"]
            },
            {
                "questionId": "Q0302",
                "content": "计算log₂8 + log₃(1/9)的值为？",
                "type": "填空题",
                "options": [],
                "score": 10,
                "answer": "1",
                "analysis": "log₂8=3（2³=8），log₃(1/9)=log₃3⁻²=-2，和为3-2=1。",
                "difficulty": "基础",
                "tags": ["对数运算", "对数性质"]
            },
            {
                "questionId": "Q0303",
                "content": "比较大小：0.3^0.4 ___ 0.4^0.3（填>、<或=）",
                "type": "填空题",
                "options": [],
                "score": 10,
                "answer": "<",
                "analysis": "0.3^0.4 < 0.3^0.3（指数函数底数<1时，指数大值小），0.3^0.3 < 0.4^0.3（幂函数指数>0时，底数大值大），故0.3^0.4 < 0.4^0.3。",
                "difficulty": "较高",
                "tags": ["指数比较", "幂函数性质"]
            }
        ],
        "createTime": new Date("2025-06-26T10:22:35Z"),
        "startTime": new Date("2025-07-26T10:22:35Z"),
        "status": "已发布",
        "duration": 60,
        "totalScore": 30
    },
    {
        "_id": ObjectId("649a1c85f3f2b4c1e6d7b314"),
        "title": "高中数学必修一函数图像与零点综合测试",
        "description": "",
        "courseId": 123456,
        "teacherId": 123456,
        "questions": [
            {
                "questionId": "Q0401",
                "content": "函数f(x)=x³-3x的零点个数为？",
                "type": "单选题",
                "options": [{
                    "optionId": "A",
                    "content": "1"
                }, {
                    "optionId": "B",
                    "content": "2"
                }, {
                    "optionId": "C",
                    "content": "3"
                }, {
                    "optionId": "D",
                    "content": "4"
                }],
                "score": 10,
                "answer": "C",
                "analysis": "f(x)=x(x²-3)=x(x-√3)(x+√3)，零点为x=0,√3,-√3，共3个。",
                "difficulty": "基础",
                "tags": ["函数零点", "因式分解"]
            },
            {
                "questionId": "Q0402",
                "content": "函数f(x)=2^x + x - 4的零点所在区间为？",
                "type": "单选题",
                "options": [{
                    "optionId": "A",
                    "content": "(1,2)"
                }, {
                    "optionId": "B",
                    "content": "(1,3)"
                }, {
                    "optionId": "C",
                    "content": "(2,3)"
                }, {
                    "optionId": "D",
                    "content": "(2,4)"
                }],
                "score": 10,
                "answer": "B",
                "analysis": "f(1)=2+1-4=-1<0，f(2)=4+2-4=2>0，由零点存在定理，零点在(1,2)。",
                "difficulty": "中等",
                "tags": ["零点存在定理", "函数求值"]
            },
            {
                "questionId": "Q0403",
                "content": "画出函数f(x)=|x²-2x|的图像，并指出其单调递减区间。",
                "type": "解答题",
                "options": [],
                "score": 10,
                "answer": "单调递减区间：(-∞,0]和[1,2]",
                "analysis": "先画y=x²-2x的图像，再将x轴下方部分翻折到上方；对称轴x=1，结合图像得递减区间。",
                "difficulty": "中等",
                "tags": ["绝对值函数图像", "单调性区间"]
            }
        ],
        "createTime": new Date("2025-06-26T10:22:35Z"),
        "startTime": new Date("2025-07-26T10:22:35Z"),
        "status": "已发布",
        "duration": 60,
        "totalScore": 30
    },
    {
        "_id": ObjectId("649a1c85f3f2b4c1e6d7b315"),
        "title": "高中数学必修一函数实际应用综合测试",
        "description": "",
        "courseId": 123456,
        "teacherId": 123456,
        "questions": [
            {
                "questionId": "Q0501",
                "content": "某商品定价为每件60元时，每周可卖出300件；价格每上涨1元，销量减少10件。求利润y与售价x的函数关系式（成本为每件40元）。",
                "type": "解答题",
                "options": [],
                "score": 10,
                "answer": "y=-10x²+1300x-36000",
                "analysis": "销量=300-10(x-60)=900-10x，利润=(x-40)(900-10x)=-10x²+1300x-36000。",
                "difficulty": "中等",
                "tags": ["二次函数应用", "利润问题"]
            },
            {
                "questionId": "Q0502",
                "content": "某细胞分裂时，由1个分裂成2个，2个分裂成4个…设分裂次数为x，细胞个数为y，写出y关于x的函数关系式，并求分裂10次后的细胞个数。",
                "type": "解答题",
                "options": [],
                "score": 10,
                "answer": "y=2^x，1024个",
                "analysis": "每次分裂个数翻倍，指数函数y=2^x，x=10时y=2^10=1024。",
                "difficulty": "基础",
                "tags": ["指数函数应用", "细胞分裂"]
            },
            {
                "questionId": "Q0503",
                "content": "某工厂生产某种产品，固定成本为20000元，每生产1件成本增加100元，已知总收益R与产量x的函数为R(x)=400x - 0.5x²（0≤x≤400），求利润最大时的产量。",
                "type": "解答题",
                "options": [],
                "score": 10,
                "answer": "300件",
                "analysis": "利润L(x)=R(x)-(20000+100x)=-0.5x²+300x-20000，对称轴x=300，开口向下，故x=300时利润最大。",
                "difficulty": "较高",
                "tags": ["二次函数最值", "生产利润"]
            }
        ],
        "createTime": new Date("2025-06-26T10:22:35Z"),
        "startTime": new Date("2025-07-26T10:22:35Z"),
        "status": "已发布",
        "duration": 60,
        "totalScore": 30
    }])

```

连接mysql，执行下面的语句

```mysql
create schema ai;
use ai;
create table user_chat
(
    user_id bigint not null comment '用户id',
    chat_id varchar(12) not null,
    primary key (user_id, chat_id)
)
    comment '用户id与AIchatId对应表';

create schema exam;
use exam;
create table answer
(
    paper_id    varchar(24)       not null comment '试卷id',
    question_id varchar(255)      not null comment '问题id',
    user_id     bigint            not null comment '用户id',
    answer      text              null comment '答案',
    answer_time datetime          not null comment '回答时间',
    score       tinyint default 0 null comment '得分',
    primary key (paper_id, question_id, user_id)
)
    comment '答案表';

create index user_id
    on answer (user_id);

create table student_papers
(
    student_id  bigint             not null,
    paper_id    varchar(24)        not null,
    status      tinyint default 0  not null comment '状态，0为未完成，1为完成',
    submit_time datetime           null comment '提交时间',
    total_score tinyint default -1 null,
    primary key (student_id, paper_id)
)
    comment '学生与试卷的关联表';

create schema homework;
use homework;
create table assignments
(
    assignment_id          bigint       not null comment '作业唯一标识符'
        primary key,
    course_id              bigint       not null comment '所属课程ID',
    teacher_id             bigint       not null comment '发布教师ID',
    title                  varchar(255) not null comment '作业标题',
    content                text         null comment '作业描述/题目内容',
    max_number_submissions int          null comment '最大提交次数',
    max_chance_attempts    int          null comment '最大批改机会次数',
    end_date               datetime     null comment '截止日期',
    attachments_json       json         null comment '附件列表 (JSON数组)'
)
    row_format = DYNAMIC;

create table assignment_submissions
(
    submission_id          bigint                                  not null comment '作业提交唯一标识符'
        primary key,
    assignment_id          bigint                                  not null comment '所属作业ID',
    student_id             bigint                                  not null comment '提交学生ID',
    submission_number      int                                     not null comment '提交次数 (第几次提交)',
    answer_content         text                                    null comment '主观题答案内容',
    answers_objective_json json                                    null comment '客观题答案 (JSON对象)',
    submitted_at           datetime      default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '提交时间',
    attachments_json       json                                    null comment '提交文件附件列表 (JSON数组)',
    status                 varchar(50)                             not null comment '提交状态 (e.g., submitted, graded, returned)',
    score                  decimal(5, 2) default (-(1))            null comment '作业得分',
    feedback               text                                    null comment '教师评语',
    graded_by_id           bigint                                  null comment '批改教师ID',
    graded_at              datetime                                null comment '批改时间',
    constraint fk_submissions_assignment
        foreign key (assignment_id) references assignments (assignment_id)
)
    row_format = DYNAMIC;

create index fk_submissions_grader
    on assignment_submissions (graded_by_id);

create index fk_submissions_student
    on assignment_submissions (student_id);

create index fk_assignments_course
    on assignments (course_id);

create index fk_assignments_teacher
    on assignments (teacher_id);
create schema knowledge;
use knowledge;
create table knowledge_comments
(
    id         bigint auto_increment comment '评论ID'
        primary key,
    kp_id      bigint                             not null comment '知识点ID',
    user_id    bigint                             not null comment '评论者ID',
    content    text                               not null comment '评论内容',
    parent_id  bigint                             null comment '父评论ID（用于回复功能）',
    created_at datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updated_at datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '知识点评论表';

create index idx_created_at
    on knowledge_comments (created_at);

create index idx_kp_id
    on knowledge_comments (kp_id);

create index idx_parent_id
    on knowledge_comments (parent_id);

create index idx_user_id
    on knowledge_comments (user_id);

create table knowledge_points
(
    kp_id            bigint                             not null comment '知识点唯一标识符'
        primary key,
    title            varchar(255)                       not null comment '知识点标题',
    content          text                               null comment '知识点内容',
    author_id        bigint                             not null comment '创建者ID',
    attachments_json json                               null comment '附件列表 (JSON数组)',
    tags             json                               null comment '标签列表 (JSON数组)',
    status           varchar(50)                        not null comment '知识点状态 (e.g., draft, published)',
    created_at       datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updated_at       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '最后更新时间'
)
    row_format = DYNAMIC;

create index fk_kps_author
    on knowledge_points (author_id);

create table knowledge_user_favorites
(
    id         bigint auto_increment comment '主键ID'
        primary key,
    kp_id      bigint                             not null comment '知识点ID',
    user_id    bigint                             not null comment '用户ID',
    created_at datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    constraint uk_kp_user
        unique (kp_id, user_id) comment '知识点和用户的唯一约束'
)
    comment '用户收藏表';

create index idx_kp_id
    on knowledge_user_favorites (kp_id);

create index idx_user_id
    on knowledge_user_favorites (user_id);

create table knowledge_user_history
(
    id         bigint auto_increment comment '主键ID'
        primary key,
    kp_id      bigint                             not null comment '知识点ID',
    user_id    bigint                             not null comment '用户ID',
    viewed_at  datetime default CURRENT_TIMESTAMP not null comment '浏览时间',
    duration   int      default 0                 null comment '停留时长（秒）',
    created_at datetime default CURRENT_TIMESTAMP not null comment '创建时间'
)
    comment '用户浏览历史表';

create index idx_kp_id
    on knowledge_user_history (kp_id);

create index idx_user_id
    on knowledge_user_history (user_id);

create index idx_viewed_at
    on knowledge_user_history (viewed_at);

create table knowledge_user_likes
(
    id         bigint auto_increment comment '主键ID'
        primary key,
    kp_id      bigint                             not null comment '知识点ID',
    user_id    bigint                             not null comment '用户ID',
    created_at datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    constraint uk_kp_user
        unique (kp_id, user_id) comment '知识点和用户的唯一约束'
)
    comment '用户点赞表';

create index idx_kp_id
    on knowledge_user_likes (kp_id);

create index idx_user_id
    on knowledge_user_likes (user_id);



```

