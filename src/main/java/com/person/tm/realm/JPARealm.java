package com.person.tm.realm;

import com.person.tm.pojo.User;
import com.person.tm.service.UserService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.elasticsearch.common.util.ByteUtils;
import org.springframework.beans.factory.annotation.Autowired;
/* realm 中文意思：领域：
   JPARealm 在这里相当于一个中介：
   系统获取的用户名密码 ，给 shiro ，shiro 给realm 用来判断数据库中是否存在。
*/
public class JPARealm extends AuthorizingRealm {

    @Autowired
    UserService userService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        return simpleAuthorizationInfo;
    }

    //验证账号密码是否一致
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //登录的账号
        String userName = token.getPrincipal().toString();
        User user = userService.getByName(userName);
        String password = user.getPassword();
        String salt = user.getSalt();

        //用户名，密码，salt（盐），getname()
        // SimpleAuthenticationInfo 作用是 数据库中加密后的密码 与账号 原密码进行判断 是否一致。
        //salt：盐。因为使用md5加密 原密码 时 加入了salt 和加密次数 生成了 数据库中加密后的密码。
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(userName, password,
                ByteSource.Util.bytes(salt),
                getName());

        return simpleAuthenticationInfo;
    }
}
