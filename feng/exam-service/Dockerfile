# 第一阶段：构建应用
FROM maven:3.8.6-openjdk-17-slim AS build
WORKDIR /app

# 复制 Maven 配置文件并下载依赖
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码并构建应用
COPY src ./src
RUN mvn package -DskipTests

# 第二阶段：创建运行时镜像
FROM openjdk:17-jdk-slim
WORKDIR /app

# 从构建阶段复制打包好的 JAR 文件
COPY --from=build /app/target/*.jar app.jar

# 暴露应用端口
EXPOSE 8080

# 设置环境变量
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]