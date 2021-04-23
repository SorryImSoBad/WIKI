package com.example.demo.service;

import com.example.demo.domain.Ebook;
import com.example.demo.domain.EbookExample;
import com.example.demo.mapper.EbookMapper;
import com.example.demo.req.EbookReq;
import com.example.demo.resp.EbookResp;
import com.example.demo.util.CopyUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EbookService {

    private static final Logger LOG = LoggerFactory.getLogger(EbookService.class);

    @Resource
    private EbookMapper ebookMapper;

    public List<EbookResp> list(EbookReq req){
        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        if (!ObjectUtils.isEmpty(req.getName()))
            criteria.andNameLike('%'+req.getName()+'%');
        PageHelper.startPage(1,3);
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);

        PageInfo<Ebook> pageInfo = new PageInfo<>(ebookList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());


//        List<EbookResp> respList = new ArrayList<>();
//        for(Ebook ebook: ebookList){
////            EbookResp ebookResp = new EbookResp();
////            BeanUtils.copyProperties(ebook, ebookResp);
//            //对象复制
//            EbookResp ebookResp = CopyUtil.copy(ebook, EbookResp.class);
//            respList.add(ebookResp);
//         }
        //列表复制
        List<EbookResp> List = CopyUtil.copyList(ebookList, EbookResp.class);

        return List;
    }
}
