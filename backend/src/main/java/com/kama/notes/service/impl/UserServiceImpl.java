package com.kama.notes.service.impl;

import com.kama.notes.annotation.NeedLogin;
import com.kama.notes.model.base.ApiResponse;
import com.kama.notes.model.base.Pagination;
import com.kama.notes.model.dto.user.LoginRequest;
import com.kama.notes.model.dto.user.RegisterRequest;
import com.kama.notes.model.dto.user.UpdateUserRequest;
import com.kama.notes.model.dto.user.UserQueryParam;
import com.kama.notes.model.entity.User;
import com.kama.notes.mapper.UserMapper;
import com.kama.notes.model.vo.user.AvatarVO;
import com.kama.notes.model.vo.user.RegisterVO;
import com.kama.notes.model.vo.user.LoginUserVO;
import com.kama.notes.model.vo.user.UserVO;
import com.kama.notes.scope.RequestScopeData;
import com.kama.notes.service.FileService;
import com.kama.notes.service.UserService;
import com.kama.notes.utils.ApiResponseUtil;
import com.kama.notes.utils.JwtUtil;
import com.kama.notes.utils.PaginationUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;


@Log4j2
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private FileService fileService;

    @Autowired
    private RequestScopeData requestScopeData;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<RegisterVO> register(RegisterRequest request) {
        // 检查账号是否已存在
        // TODO: 可以优化
        User existingUser = userMapper.findByAccount(request.getAccount());
        if (existingUser != null) {
            return ApiResponseUtil.error("账号重复");
        }

        // 创建新用户
        User user = new User();

        BeanUtils.copyProperties(request, user);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        try {
            // 保存用户
            userMapper.insert(user);

            String token = jwtUtil.generateToken(user.getUserId());

            RegisterVO registerVO = new RegisterVO();
            BeanUtils.copyProperties(user, registerVO);

            userMapper.updateLastLoginAt(user.getUserId());

            return ApiResponseUtil.success("注册成功", registerVO, token);
        } catch (Exception e) {
            log.error("注册失败", e);
            return ApiResponseUtil.error("注册失败，请稍后再试");
        }
    }

    @Override
    public ApiResponse<LoginUserVO> login(LoginRequest request) {

        User user = userMapper.findByAccount(request.getAccount());

        // 验证账号以及密码
        if (user == null) {
            return ApiResponseUtil.error("账号不存在");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ApiResponseUtil.error("密码错误");
        }

        // 生成JWT
        String token = jwtUtil.generateToken(user.getUserId());

        LoginUserVO userVO = new LoginUserVO();
        BeanUtils.copyProperties(user, userVO);

        // 更新登录时间
        userMapper.updateLastLoginAt(user.getUserId());

        return ApiResponseUtil.success("登录成功", userVO, token);
    }


    @Override
    public ApiResponse<LoginUserVO> whoami() {
        Long userId = requestScopeData.getUserId();

        if (userId == null) {
            return ApiResponseUtil.error("用户 ID 异常");
        }

        try {
            // 查询用户信息
            User user = userMapper.findById(userId);
            if (user == null) {
                return ApiResponseUtil.error("用户不存在");
            }

            // 生成新的 JWT
            String newToken = jwtUtil.generateToken(userId);
            if (newToken == null) {
                return ApiResponseUtil.error("系统错误");
            }

            // 映射用户信息到 VO
            LoginUserVO userVO = new LoginUserVO();
            BeanUtils.copyProperties(user, userVO);

            // 更新登录时间并返回响应
            userMapper.updateLastLoginAt(userId);
            return ApiResponseUtil.success("自动登录成功", userVO, newToken);
        } catch (Exception e) {
            return ApiResponseUtil.error("系统错误");
        }
    }

    @Override
    public ApiResponse<UserVO> getUserInfo(Long userId) {

        User user = userMapper.findById(userId);

        if (user == null) {
            return ApiResponseUtil.error("用户不存在");
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        return ApiResponseUtil.success("获取用户信息成功", userVO);
    }

    @Override
    @Transactional
    @NeedLogin
    public ApiResponse<LoginUserVO> updateUserInfo(UpdateUserRequest request) {
        Long userId = requestScopeData.getUserId();

        User user = new User();
        BeanUtils.copyProperties(request, user);
        user.setUserId(userId);

        System.out.println(user);

        try {
            userMapper.update(user);
            return ApiResponseUtil.success("更新成功");
        } catch (Exception e) {
            return ApiResponseUtil.error("更新失败");
        }
    }

    @Override
    public Map<Long, User> getUserMapByIds(List<Long> authorIds) {

        // 处理空数组的情况
        if (authorIds.isEmpty()) return Collections.emptyMap();

        // 查询用户列表
        List<User> users = userMapper.findByIdBatch(authorIds);

        return users.stream()
                .collect(Collectors.toMap(User::getUserId, user -> user));
    }

    @Override
    public ApiResponse<List<User>> getUserList(UserQueryParam userQueryParam) {

        // 分页数据
        int total = userMapper.countByQueryParam(userQueryParam);
        int offset = PaginationUtils.calculateOffset(userQueryParam.getPage(), userQueryParam.getPageSize());
        Pagination pagination = new Pagination(userQueryParam.getPage(), userQueryParam.getPageSize(), total);

        try {
            List<User> users = userMapper.findByQueryParam(userQueryParam, userQueryParam.getPageSize(), offset);

            return ApiResponseUtil.success("获取用户列表成功", users, pagination);
        } catch (Exception e) {
            return ApiResponseUtil.error(e.getMessage());
        }
    }

    @Override
    public ApiResponse<AvatarVO> uploadAvatar(MultipartFile file) {
        try {
            String url = fileService.uploadImage(file);
            AvatarVO avatarVO = new AvatarVO();
            avatarVO.setUrl(url);
            return ApiResponseUtil.success("上传成功", avatarVO);
        } catch (Exception e) {
            return ApiResponseUtil.error(e.getMessage());
        }
    }
}
