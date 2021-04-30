package com.example.demo.service;

import com.example.demo.domain.Category;
import com.example.demo.domain.CategoryExample;
import com.example.demo.mapper.CategoryMapper;
import com.example.demo.req.CategoryQueryReq;
import com.example.demo.req.CategorySaveReq;
import com.example.demo.resp.CategoryQueryResp;
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
public class CategoryService {

    private static final Logger LOG = LoggerFactory.getLogger(CategoryService.class);

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SnowFlake snowFlake;

    //查询
    public List<CategoryQueryResp> all(CategoryQueryReq req){
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.setOrderByClause("sort asc");
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);

        //列表复制
        List<CategoryQueryResp> list = CopyUtil.copyList(categoryList, CategoryQueryResp.class);

        return list;
    }

    public PageResp<CategoryQueryResp> list(CategoryQueryReq req){
        CategoryExample categoryExample = new CategoryExample();
        categoryExample.setOrderByClause("sort asc");
        CategoryExample.Criteria criteria = categoryExample.createCriteria();
        if (!ObjectUtils.isEmpty(req.getName()))
            criteria.andNameLike('%'+req.getName()+'%');
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Category> categoryList = categoryMapper.selectByExample(categoryExample);

        PageInfo<Category> pageInfo = new PageInfo<>(categoryList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());


//        List<CategoryResp> respList = new ArrayList<>();
//        for(Category category: categoryList){
////            CategoryResp categoryResp = new CategoryResp();
////            BeanUtils.copyProperties(category, categoryResp);
//            //对象复制
//            CategoryResp categoryResp = CopyUtil.copy(category, CategoryResp.class);
//            respList.add(categoryResp);
//         }
        //列表复制
        PageResp<CategoryQueryResp> PageResp = new PageResp();
        //return CopyUtil.copyList(categoryList, CategoryResp.class);
        PageResp.setTotal(pageInfo.getTotal());
        PageResp.setList(CopyUtil.copyList(categoryList, CategoryQueryResp.class));
        return PageResp;
    }

    //保存
    public void save(CategorySaveReq req){
        Category category = CopyUtil.copy(req, Category.class);
        if(ObjectUtils.isEmpty(req.getId())){
            category.setId(snowFlake.nextId());
            categoryMapper.insert(category);
        }else {
            categoryMapper.updateByPrimaryKey(category);
        }
    }

    //删除
    public void delete(Long id){
        categoryMapper.deleteByPrimaryKey(id);
    }
}
