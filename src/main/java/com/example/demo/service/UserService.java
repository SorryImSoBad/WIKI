package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.domain.UserExample;
import com.example.demo.mapper.UserMapper;
import com.example.demo.req.UserQueryReq;
import com.example.demo.req.UserSaveReq;
import com.example.demo.resp.UserQueryResp;
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
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Resource
    private UserMapper userMapper;

    @Resource
    private SnowFlake snowFlake;

    //查询
    public PageResp<UserQueryResp> list(UserQueryReq req){
        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();
        if (!ObjectUtils.isEmpty(req.getLoginName()))
            criteria.andLoginNameEqualTo(req.getLoginName());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<User> userList = userMapper.selectByExample(userExample);

        PageInfo<User> pageInfo = new PageInfo<>(userList);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数：{}", pageInfo.getPages());


//        List<UserResp> respList = new ArrayList<>();
//        for(User user: userList){
////            UserResp userResp = new UserResp();
////            BeanUtils.copyProperties(user, userResp);
//            //对象复制
//            UserResp userResp = CopyUtil.copy(user, UserResp.class);
//            respList.add(userResp);
//         }
        //列表复制
        PageResp<UserQueryResp> PageResp = new PageResp();
        // return CopyUtil.copyList(userList, UserResp.class);
        PageResp.setTotal(pageInfo.getTotal());
        PageResp.setList(CopyUtil.copyList(userList, UserQueryResp.class));
        return PageResp;
    }

    //保存
    public void save(UserSaveReq req){
        User user = CopyUtil.copy(req, User.class);
        if(ObjectUtils.isEmpty(req.getId())){
            user.setId(snowFlake.nextId());
            userMapper.insert(user);
        }else {
            userMapper.updateByPrimaryKey(user);
        }
    }

    //删除
    public void delete(Long id){
        userMapper.deleteByPrimaryKey(id);
    }
}
