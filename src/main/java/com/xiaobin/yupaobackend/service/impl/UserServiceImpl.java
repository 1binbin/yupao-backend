package com.xiaobin.yupaobackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaobin.yupaobackend.common.ErrorCode;
import com.xiaobin.yupaobackend.exception.BusinessException;
import com.xiaobin.yupaobackend.mapper.UserMapper;
import com.xiaobin.yupaobackend.model.domain.User;
import com.xiaobin.yupaobackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.xiaobin.yupaobackend.contant.UserConstant.SALT;
import static com.xiaobin.yupaobackend.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
 *
 * @author hongxiaobin
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2023-02-26 16:59:19
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Resource
    private UserMapper userMapper;


    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        // 1.校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号小于4位");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码小于8位");
        }
        if (planetCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户编号大于5位");
        }
        // 2.校验账户不能包含特殊字符
        String pattern = "^[\\u4E00-\\u9FA5A-Za-z0-9]+$";
        boolean matches = Pattern.matches(pattern, userAccount);
        if (!matches) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户包含特殊字符");
        }
        // 3.校验密码和二次密码是否相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码输入不相同");
        }
        // 4.账户不能重复
        QueryWrapper<User> userAccountQueryWrapper = new QueryWrapper<>();
        userAccountQueryWrapper.eq("userAccount", userAccount);
        long userAccountCount = userMapper.selectCount(userAccountQueryWrapper);
        if (userAccountCount > 0) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "账户重复");
        }
        // 5.编号不能重复
        QueryWrapper<User> planetCodeQueryWrapper = new QueryWrapper<>();
        planetCodeQueryWrapper.eq("planetCode", planetCode);
        long planetCodeCount = userMapper.selectCount(planetCodeQueryWrapper);
        if (planetCodeCount > 0) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户编号重复");
        }

        // 6.密码加密（一般使用MD5，这里使用一个工具库）
        String digestPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // 7.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(digestPassword);
        user.setPlanetCode(planetCode);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "插入数据异常");
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1.校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号小于4位");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码小于8位");
        }
        // 2.校验账户不能包含特殊字符
        String pattern = "^[\\u4E00-\\u9FA5A-Za-z0-9]+$";
        boolean matches = Pattern.matches(pattern, userAccount);
        if (!matches) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户包含特殊字符");
        }

        String digestPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount", userAccount);
        userQueryWrapper.eq("userPassword", digestPassword);

        User user = userMapper.selectOne(userQueryWrapper);
        if (user == null) {
            log.info("User login error, account or password error");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码输入不相同");
        }
        // TODO: 2023/2/26 单位之间内限制错误登录次数

        // 3.记录用户的登录状态
        User safeUser = getSafeUser(user);
        HttpSession session = request.getSession();
        session.setAttribute(USER_LOGIN_STATE, safeUser);
        // 4.登录成功返回脱密信息
        return safeUser;
    }


    /**
     * 用户注销
     *
     * @param request 请求域
     * @return
     */
    @Override
    public int userLoginOut(HttpServletRequest request) {
        if (request == null) {
            return 0;
        }
        // 移除session中保存的用户信息即为注销
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 根据标签搜索用户【使用内存查询】
     *
     * @param tagNameList 标签列表
     * @return 脱密的用户信息
     */
    @Override
    public List<User> searchUserByTags(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签列表为空");
        }
        // 内存查询
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        // 1.查询所有用户
        List<User> userList = userMapper.selectList(userQueryWrapper);
        // 2.在内存中进行运算
        Gson gson = new Gson();
        // 使用集合的语法糖过滤掉不需要的信息
        return userList.stream().filter(user -> {
            String userTags = user.getTags();
            Set<String> tempTagNameSet = gson.fromJson(userTags, new TypeToken<Set<String>>() {
            }.getType());
            // 判空
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
            for (String tagName : tagNameList) {
                //    3.过滤掉不需要的tagName
                if (!tempTagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafeUser).collect(Collectors.toList());
    }

    /**
     * 根据标签搜索用户【根据SQL语句】
     *
     * @param tagNameList 标签列表
     * @return 脱密的用户信息
     */
    @Deprecated
    private List<User> searchUserByTagsBySQL(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签列表为空");
        }
        // SQL查询
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        // 拼接like查询
        for (String tagName : tagNameList) {
            // 等价于 like ····· and like ······
            userQueryWrapper = userQueryWrapper.like("tags", tagName);
        }
        List<User> userList = userMapper.selectList(userQueryWrapper);
        return userList.stream().map(this::getSafeUser).collect(Collectors.toList());
    }

    /**
     * 用户脱敏
     *
     * @param user 没有脱敏的用户信息
     * @return 已经脱敏的用户信息
     */
    @Override
    public User getSafeUser(User user) {
        // 在Controller层和Service层都必须对用户进行判空
        if (user == null) {
            return null;
        }
        User handlerUser = new User();
        handlerUser.setId(user.getId());
        handlerUser.setUsername(user.getUsername());
        handlerUser.setUserAccount(user.getUserAccount());
        handlerUser.setAvatarUrl(user.getAvatarUrl());
        handlerUser.setGender(user.getGender());
        handlerUser.setPhone(user.getPhone());
        handlerUser.setEmail(user.getEmail());
        handlerUser.setCreateTime(user.getCreateTime());
        handlerUser.setUserStatus(user.getUserStatus());
        handlerUser.setPlanetCode(user.getPlanetCode());
        handlerUser.setUserRole(user.getUserRole());
        handlerUser.setTags(user.getTags());
        return handlerUser;
    }
}




