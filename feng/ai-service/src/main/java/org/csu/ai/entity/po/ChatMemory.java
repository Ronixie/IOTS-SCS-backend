package org.csu.ai.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.MimeType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @since 2025-06-27
 */
@Data
@Document(collection = "chat_memory")
@NoArgsConstructor
public class ChatMemory{

    @Id
    private String id;

    /**
     * 会话唯一标识（区分不同对话）
     */
    private String sessionId;


    /**
     * 角色（USER/ASSISTANT/SYSTEM）
     */
    private String role;

    /**
     * 对话内容
     */
    private String content;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt=LocalDateTime.now();

    /**
     * 附件列表
     */
    @JsonIgnore
    private List<MetaData> dataList = new ArrayList<>();

    @PersistenceConstructor
    public ChatMemory(String role, String id, String sessionId, String content, LocalDateTime createdAt, List<MetaData> dataList) {
        this.role = role;
        this.id = id;
        this.sessionId = sessionId;
        this.content = content;
        this.createdAt = createdAt;
        this.dataList = dataList;
    }

    public ChatMemory(String sessionId, String role, String content) {
        this.sessionId = sessionId;
        this.role = role;
        this.content = content;
    }

    /**
     * 保存附件
     * @param gridFsId 文件Id
     * @param type 文件类型
     */
    public void saveData(String gridFsId, MimeType type) {
        dataList.add(new MetaData(gridFsId, type.getType(), type.getSubtype()));
    }

    /**
     * 保存附件列表
     * @param gridFsIds 文件Id列表
     * @param types 文件类型列表
     */
    public void saveDataList(List<String>gridFsIds, List<MimeType> types) {
        if(gridFsIds.size()!= types.size()){
            throw new RuntimeException("gridFsIds size not equal types size");
        }
        for (int i = 0; i < gridFsIds.size(); i++) {
            saveData(gridFsIds.get(i), types.get(i));
        }
    }

    /**
     * 元数据内部类，用于存储附件相关信息
     */
    @Data
    public static class MetaData {
        /**
         * GridFS文件ID
         */
        @Field("grid_fs_id")
        String gridFsId;

        /**
         * 文件主类型
         */
        @Field("type")
        String type;

        /**
         * 文件子类型
         */
        @Field("subtype")
        String subtype;

        // 显式定义构造函数，保留参数名
        @PersistenceConstructor // 关键：标记为持久化构造函数
        public MetaData(String gridFsId, String type, String subtype) {
            this.gridFsId = gridFsId;
            this.type = type;
            this.subtype = subtype;
        }
    }
}
