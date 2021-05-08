package com.example.demo.service;

import com.example.demo.domain.Doc;
import com.example.demo.domain.DocExample;
import com.example.demo.mapper.DocMapper;
import com.example.demo.req.DocQueryReq;
import com.example.demo.req.DocSaveReq;
import com.example.demo.resp.DocQueryResp;
import com.example.demo.resp.PageResp;
import com.example.demo.util.CopyUtil;
import com.example.demo.util.SnowFlake;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DocService {

    private static final Logger LOG = LoggerFactory.getLogger(DocService.class);

    @Resource
    private DocMapper docMapper;

    @Resource
    private SnowFlake snowFlake;

    //查询
    public List<DocQueryResp> all() {
        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        List<Doc> docList = docMapper.selectByExample(docExample);

        //列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        return list;
    }

    public PageResp<DocQueryResp> list(DocQueryReq req) {
        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        DocExample.Criteria criteria = docExample.createCriteria();
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Doc> docList = docMapper.selectByExample(docExample);

        PageInfo<Doc> pageInfo = new PageInfo<>(docList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());


//        List<DocResp> respList = new ArrayList<>();
//        for(Doc doc: docList){
////            DocResp docResp = new DocResp();
////            BeanUtils.copyProperties(doc, docResp);
//            //对象复制
//            DocResp docResp = CopyUtil.copy(doc, DocResp.class);
//            respList.add(docResp);
//         }
        //列表复制
        PageResp<DocQueryResp> PageResp = new PageResp();
        //return CopyUtil.copyList(docList, DocResp.class);
        PageResp.setTotal(pageInfo.getTotal());
        PageResp.setList(CopyUtil.copyList(docList, DocQueryResp.class));
        return PageResp;
    }

    //保存
    public void save(DocSaveReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);
        if (ObjectUtils.isEmpty(req.getId())) {
            doc.setId(snowFlake.nextId());
            docMapper.insert(doc);
        } else {
            docMapper.updateByPrimaryKey(doc);
        }
    }

    //删除
    public void delete(Long id) {
        docMapper.deleteByPrimaryKey(id);
    }

    public void delete(List<String> ids) {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);
    }
}
