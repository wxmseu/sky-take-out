package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.exception.UsernameDuplicateException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // 通过md5加密后判断密码正确行
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!md5Password.equals(employee.getPassword())){
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    public void save(EmployeeDTO employeeDTO){
        log.info("service层中当前线程的id是：{}", Thread.currentThread().getId());
        String username = employeeDTO.getUsername();
        Employee employee1 = employeeMapper.getByUsername(username);
        if (!Objects.isNull(employee1)){
            throw new UsernameDuplicateException("用户名重复");
        }
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        // 设置默认状态，创建数据库是status有默认值，此处可以省略
        employee.setStatus(StatusConstant.ENABLE);
        // 设置默认密码
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        LocalDateTime localDateTime = LocalDateTime.now();
        // 设置创建和修改时间
        employee.setCreateTime(localDateTime);
        employee.setUpdateTime(localDateTime);
        // 设置修改人
        // 获取修改人信息
        Long emp_id = BaseContext.getCurrentId();
        employee.setCreateUser(emp_id);
        employee.setUpdateUser(emp_id);
        employeeMapper.save(employee);
    }


    public PageResult employeeInfo(EmployeePageQueryDTO employeePageQueryDTO){

        // 开始分页查询
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        Page<Employee> pageQuery = employeeMapper.pageEmployeeInfo(employeePageQueryDTO);

        // 获取分页信息
        long total = pageQuery.getTotal();
        List<Employee> records = pageQuery.getResult();

        return new PageResult(total, records);

    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Employee employee = Employee.builder()
                .status(status)
                .id(id)
                .build();

        employeeMapper.update(employee);
    }

    @Override
    public Employee queryEmployeeDetailById(Integer id) {

        return employeeMapper.queryEmployeeDetailById(id);
    }

    @Override
    public void editEmployInfo(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.update(employee);

    }
}
