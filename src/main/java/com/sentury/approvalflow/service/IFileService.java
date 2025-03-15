package com.sentury.approvalflow.service;

import com.sentury.approvalflow.common.dto.R2;

/**
 * @author Huijun Zhao
 * @description
 * @date 2023-11-02 09:54
 */
public interface IFileService {
    /**
     * 保存文件
     *
     * @param fileName
     * @return
     */
    R2<String> save(byte[] data, String fileName);

}
