package org.csu.ai.service.impl;

import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GridFsService {

    private final GridFsOperations gridFsOperations; // GridFS 操作模板
    private final GridFsTemplate gridFsTemplate;
    /**
     * 根据文件 ID 查询 GridFS 文件并返回资源
     */
    public GridFsResource getFileResource(String fileId) {
        // 构建查询条件（根据 _id 查询）
        Query query = Query.query(Criteria.where("_id").is(fileId));

        // 查询文件（返回 GridFSFile 对象）
        GridFSFile gridFSFile =
                gridFsTemplate.findOne(query);

        // 将 GridFSFile 转换为 GridFsResource
        return gridFsTemplate.getResource(gridFSFile);
    }

    /**
     * 存储 List<byte[]> 到 GridFS（拆分为多个文件）
     * @param byteArrayList 二进制数据集合
     * @param baseFileName 基础文件名（用于生成唯一文件名）
     * @return 文件 ID 列表
     */
    public List<String> saveByteArrayListAsMultipleFiles(List<byte[]> byteArrayList, String baseFileName) {
        List<String> fileIds = new ArrayList<>();

        for (int i = 0; i < byteArrayList.size(); i++) {
            byte[] data = byteArrayList.get(i);
            String fileName = baseFileName + "_part" + i; // 生成唯一文件名

            try (InputStream inputStream = new ByteArrayInputStream(data)) {
                ObjectId fileId = gridFsOperations.store(
                        inputStream,
                        fileName,
                        "application/octet-stream"
                );
                fileIds.add(fileId.toString());
            } catch (Exception e) {
                throw new RuntimeException("存储文件 " + fileName + " 失败", e);
            }
        }

        return fileIds;
    }

    /**
     * 从 GridFS 读取文件并转换为 byte[]
     */
    public byte[] getFileAsByteArray(String fileId) {
        try {
            GridFsResource resource = getFileResource(fileId);

            if (!resource.exists()) {
                throw new RuntimeException("文件不存在：" + fileId);
            }

            // 读取文件内容为 byte[]
            return resource.getInputStream().readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("读取文件失败", e);
        }
    }
}