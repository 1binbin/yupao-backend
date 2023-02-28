package com.xiaobin.yupaobackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaobin.yupaobackend.model.domain.Tag;
import com.xiaobin.yupaobackend.service.TagService;
import com.xiaobin.yupaobackend.mapper.TagMapper;
import org.springframework.stereotype.Service;

/**
* @author hongxiaobin
* @description 针对表【tag(标签)】的数据库操作Service实现
* @createDate 2023-02-28 22:14:15
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




