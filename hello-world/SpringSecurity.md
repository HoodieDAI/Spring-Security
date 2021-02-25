## 分布式认证方案选型

### 基于session

session是存储在服务器中的，在分布式环境下，如果请求的机器没有之前的session，就会重新认证

**解决方案**

1. session复制:服务器之间同步session
2. session黏贴:用户访问服务器后,该用户之后的请求强制落到这台机器上
3. session集中存储,session存入分布式缓存中,所有服务器从这里取session



**优点**

1. 安全性高

**问题**

1. 复杂的移动客户端不能有效使用
2. 不能跨域
3. 系统扩展,需要维护session复制,黏贴的容错性

### 基于token

**先说优点**

1. 扩展性强,易维护,可以存储在任何地方
2. 适合统一多端
3. 三方应用接口更合适
4. 服务端无需存储会话信息,减轻服务器压力

**缺点**

1. 数据量大
2. 每次请求都要传递

## OAuth2.0

### 包含角色

#### 客户端

​	请求资源的角色

#### 资源拥有者

​	用户，应用程序，即资源拥有者

#### 授权服务器（认证服务器）

​	对资源只能拥有的身份认证、对访问资源进行授权，认证成功向客户端发放令牌（access_tocken）

#### 资源服务器

​	存储资源的服务器

> #### 	问题：
>
> 随便一个客户端，就能接入到授权服务器吗？
>
> 肯定不行!
>
> 要具有一个作为接入==授权服务器==凭据的==身份==，才能接入==授权服务器==
>
> ​	`client_id`:客户端标识
>
> ​	`client_secret`:客户端密钥



### 认证流程

1. 客户端向==资源拥有者==发送,授权请求
2. 资源拥有者向客户端返回一个==授权许可==
3. 客户端向==授权服务器==出示授权许可
4. 认证成功，返回==令牌（Access Token）==给客户端
5. 客户端向 **资源服务器** 出示令牌
6. 资源服务器向 客户端 返回被保护的资源



# SpringSecurity

## 初识

### 开启SpringSecurity

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```



加载这个依赖后，我们调用任何接口都要通过认证

因为spring security会默认开启如下配置

```yaml
security:
  basic:
    enabled: true
```



默认的用户名是`user`

密码是开启系统生成的

![image-20210225202913521](C:\Users\51910\AppData\Roaming\Typora\typora-user-images\image-20210225202913521.png)



输入用户密码正确后，就可以成功访问接口返回结果

### 基于表单认证

可以通过继承`WebSecurityConfigureAdaper`并重写 `configure(HttpSecurity http)` 方法，来修改认证方式

其中`WebSecurityConfigureAdaper`是 Spring Security 提供的Web 安全配置适配器

```java
@Configuration
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin() // 表单方式
                .and()
                .authorizeRequests() // 授权配置
                .anyRequest()  // 所有请求
                .authenticated(); // 都需要认证
    }
}
```

### HttpBasic 认证方式（默认）

```java
@Override
protected void configure(HttpSecurity http) throws Exception {
    // http.formLogin() // 表单方式
    http.httpBasic() // HTTP Basic方式
            .and()
            .authorizeRequests() // 授权配置
            .anyRequest()  // 所有请求
            .authenticated(); // 都需要认证
}
```

### 原理剖析

![QQ截图20180707111356.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180707111356.png)

- `UserNamePasswordAuthenticationFilter` 用于处理基本的==表单==方式登录认证
- `BasicAuthenticationFilter` 用于处理 `Http Basic` 方式的登录验证

> 后面的过滤器可以通过配置开启

- `FilterSecurityInterceptor` 用于判断当前请求身份是否成功， 是否有相应权限， 如果认证失败，或者权限不足，==抛出相应异常== 给`ExceptionTranslateFilter` 处理

### Debug

我们在`/hello`服务上打个断点：

![QQ截图20180707132345.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180707132345.png)

在`FilterSecurityInterceptor`的invoke方法的`super.beforeInvocation`上打个断点：

![QQ截图20180707132824.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180707132824.png)

当这行代码执行通过后，便可以调用下一行的`doFilter`方法来真正调用`/hello`服务，否则将抛出相应的异常。

当`FilterSecurityInterceptor`抛出异常时，异常将由`ExceptionTranslateFilter`捕获并处理，所以我们在`ExceptionTranslateFilter`的`doFilter`方法`catch`代码块第一行打个断点：

![QQ截图20180707133347.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180707133347.png)

我们待会模拟的是用户未登录直接访问`/hello`，所以应该是抛出用户未认证的异常，所以接下来应该跳转到`UsernamePasswordAuthenticationFilter`处理表单方式的用户认证。在`UsernamePasswordAuthenticationFilter`的`attemptAuthentication`方法上打个断点：

![QQ截图20180707134106.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180707134106.png)

准备完毕后，我们启动项目，然后访问http://localhost:8080/hello，代码直接跳转到`FilterSecurityInteceptor`的断点上：

![QQ截图20180707134540.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180707134540.png)

往下执行，因为当前请求没有经过身份认证，所以将抛出异常并被`ExceptionTranslateFilter`捕获：

![QQ截图20180707134540.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180707134915.png)

捕获异常后重定向到登录表单登录页面，当我们在表单登录页面输入信息点login后，代码跳转到`UsernamePasswordAuthenticationFilter`过滤器的`attemptAuthentication`方法上：

![QQ截图20180707135743.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180707135743.png)

判断用户名和密码是否正确之后，代码又跳回`FilterSecurityInterceptor`的`beforeInvocation`方法执行上：

![QQ截图20180707140158.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180707140158.png)

当认证通过时，`FilterSecurityInterceptor`代码往下执行`doFilter`，然后代码最终跳转到`/hello`上：

![QQ截图20180707140532.png](https://mrbird.cc/img/QQ%E6%88%AA%E5%9B%BE20180707140532.png)

## 自定义用户认证

> #### 自定义认证其实就是自己提供，用户的登录名和密码，不使用user 和随机密码

### 自定义认证过程

实现`UserDetailService`接口 实现抽象方法 `loadUserByUsername`



`UserDetailService`接口源码

```java
public interface UserDetailsService {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
```



其中 `LoadUserByUsername` 方法返回一个 `UserDetails` 对象

这个对象是一个接口，<font color="#F22F27">包含一些用于描述用户信息的方法</font>



`UserDetails` 接口源码

```java
public interface UserDetails extends Serializable {

    Collection<? extends GrantedAuthority> getAuthorities();

    String getPassword();

    String getUsername();

    boolean isAccountNonExpired();

    boolean isAccountNonLocked();

    boolean isCredentialsNonExpired();

    boolean isEnabled();
}
```

方法含义

- `getAuthorities()` 获取用户包含的权限，返回权限集合。权限是一个继承了`GrantedAuthority` 的对象
- `getPassword()` & `getUsername()` 获取用户 密码 和 用户名 

- `isAccountNonExpired()` 返回boolean 用于判断账户是否过期 未过期true 反之false
- `isAccountNonLocked()`  返回boolean 用于判断账户是否锁定 同上
- `isCredentialsNonExpired()` 返回boolean 凭证是否过期（密码是否过期）同上
- `isEnabled()` 返回boolean 用户是否可用



实际中我们可以 自定义UserDetails实现类 也可以使用 Spirng Security 已经实现UserDetails 的`org.springframework.security.core.userdetails.User`类



### 具体步骤

- #### 实现`UserDetailService`复写 `loadUserByUsername`

  `loadUserByUsername`的参数是一个 String ，就是Username。

  在 `loadUSerByUsername`中我们需要 通过参数从数据库里面查询到用户信息（这里我们模拟就行）

  然后返回一个 实现`UserDetails`的对象， 这里使用Spring Security 自带的User，由于权限参数不能为空，

  所以 使用`AuthorityUtils.commaSeparatedStringToAuthorityList`方法模拟一个admin的权限



​			这里还注入`PasswordEncode` 对对象密码加密



MyUser实体

```java
public class MyUser implements Serializable {
    private static final long serialVersionUID = 3497935890426858541L;

    private String userName;

    private String password;

    private boolean accountNonExpired = true;

    private boolean accountNonLocked= true;

    private boolean credentialsNonExpired= true;

    private boolean enabled= true;

    // get,set略
}
```

创建`MyUserDetialService` 实现 `UserDetailService`

```
@Configuration
public class MyUserDetailService implements UserDetailsService {


    //预先配置好的密码加密Bean
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 模拟从数据库取得用户
        MyUser user = new MyUser();
        user.setUserName(username);
        user.setPassword(this.passwordEncoder.encode("123456"));
        System.out.println(user.getPassword());

        return new User(username, user.getPassword(), user.isEnabled(),
                user.isAccountNonExpired(), user.isCredentialsNonExpired(),
                user.isAccountNonLocked(), AuthorityUtils.commaSeparatedStringToAuthorityList("admin"));
    }
}
```

在这之前配置PasswordEncoder

```java
@Configuration
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    ...
}
```

### 替换默认登录页

- #### 写一个表单页，放在resources/resouces 下 并导入thymeleaf依赖 ！！！ 这个非常重要！巨坑

==必须为post请求==

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>登录</title>
<!--    <link rel="stylesheet" href="css/login.css" type="text/css">-->
</head>
<body>
<form class="login-page" action="/login" method="post">
    <div class="form">
        <h3>替换默认的 账户登录 页</h3>
        <input type="text" placeholder="用户名" name="username" required="required" />
        <input type="password" placeholder="密码" name="password" required="required" />
        <button type="submit">登录</button>
    </div>
</form>
</body>
</html>
```



- #### 配置 继承了`WebSecurityConfigurerAdapter` 的配置类

```java
@Configuration
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage("/login.html") //配置登录页面请求URL
                .loginProcessingUrl("/login") //对应页面表单 form 的action=“/login"
                .and()
                .authorizeRequests() //授权配置
                .antMatchers("/login.html").permitAll() // 跳转 /login.html 请求不会被拦截
                .anyRequest() //所有请求
                .authenticated(); //都需要认证
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
```

http://localhost:8080/hello，会看到页面已经被重定向到了http://localhost:8080/login.html：

![image-20210225215946750](C:\Users\51910\AppData\Roaming\Typora\typora-user-images\image-20210225215946750.png)

输入用户名密码 发现报错



![image-20210225220011788](C:\Users\51910\AppData\Roaming\Typora\typora-user-images\image-20210225220011788.png)



==关闭CSRF攻击防御==，配置 继承了`WebSecurityConfigurerAdapter` 的配置类

```java
@Configuration
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage("/login.html") //配置登录页面请求URL
                .loginProcessingUrl("/login") //对应页面表单 form 的action=“/login"
                .and()
                .authorizeRequests() //授权配置
                .antMatchers("/login.html").permitAll() // 跳转 /login.html 请求不会被拦截
                .anyRequest() //所有请求
                .authenticated() //都需要认证
                .and().csrf().disable();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
```





- ### 现在有如下需求：

  在未登录的情况下 访问 xxx.html 等html资源时需要跳转到登录页（login.html），否则返回JSON数据，状态码401



- #### 1.  这时候需要将配置的`loginPage()` 设置成一个处理 请求的接口 `/authentication/require` 并且在 `antMachers()` 加入改url避免被拦截

```java
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
//                .loginPage("/login.html") //配置登录页面请求URL
                .loginPage("/authentication/require") //没有登录跳转的url
                .loginProcessingUrl("/login") //对应页面表单 form 的action=“/login"
                .and()
                .authorizeRequests() //授权配置
                .antMatchers("/authentication/require","/login.html").permitAll() // 跳转 /login.html 请求不会被拦截
                .anyRequest() //所有请求
                .authenticated() //都需要认证
                .and().csrf().disable();
    }
```



- #### 2. 定义一个控制器BrowserSecurityController 处理 `/authentication/require`请求

```java
@RestController
public class BrowserSecurityController {

    private RequestCache requestCache = new HttpSessionRequestCache();
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @GetMapping("/authentication/require")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String requireAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null) {
            String targetUrl = savedRequest.getRedirectUrl();
            if (StringUtils.endsWithIgnoreCase(targetUrl, "html")) {
                redirectStrategy.sendRedirect(request, response, "/login.html");
            }
        }
        return "访问的资源需要身份认证";
    }

}
```

在没有登录的时候，会跳转到`configure()`配置的`loginPage()` 的 url

- `HttpSessionRequestCache` 是 Spring Security 提供的用于缓存请求的对象，用它调用`getRequest()` 可以获取本次请求HTTP信息。
- `DefaultRedirectStrategy` 的  `sendRedirect()` 是 Spring Security 提供的用于处理重定向的方法

- `@ResponseStatus` 是 Spring 提供的改变状态码的注解，这里将相应状态码设置成`HttpStatus.UNAUTHORIZED` 401

- `StringUtils` Spring提供的处理 String对象的工具类



> #### 效果：当我们请求 xxx.html时
>
> 1. 由于没有登录跳转至 `http://localhost:8080/authentication/require`
>
> 2. 此时targetUrl为 `http://localhost:8080/xxx.html`
> 3. 判断targetUrl 是否以html结尾
>    1. 是: 重定向到 login.html
>    2. 否: return "访问的资源需要身份认证"到页面

### 处理成功和失败

Spring Security 为我们提供了一套默认的处理成功和失败 的方法

当我们没有登录，会自己跳转到登录页面，登录成功后再跳转回来。

登陆失败，就会提示错误的页面



我们可以自定义处理成功和失败的机制 

#### 改变成功机制

##### 打印 认证信息 `Authentication`

- ##### 实现`AuthenticationSuccessHandler` 接口 的`onAuthenticationSuccess` 方法

```java
@Component//这个类需要注入配置类，所以放入容器
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private ObjectMapper mapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(mapper.writeValueAsString(authentication));
    }
}
```

- `ObjectMapper` fastjson处理json的对象，引入Spring就有这个依赖
- `Authentication` 包含 认证信息 比如IP sessionI等 也包含用户信息(前面的SpringSecurity 自带的 User对象(UserDetails))



> 登录成功后显示
>
> ```json
> {
>   "authorities": [
>     {
>       "authority": "admin"
>     }
>   ],
>   "details": {
>     "remoteAddress": "0:0:0:0:0:0:0:1",
>     "sessionId": "CE96E37F38DD625710FFFE50826BD5C2"
>   },
>   "authenticated": true,
>   "principal": {
>     "password": null,
>     "username": "xch",
>     "authorities": [
>       {
>         "authority": "admin"
>       }
>     ],
>     "accountNonExpired": true,
>     "accountNonLocked": true,
>     "credentialsNonExpired": true,
>     "enabled": true
>   },
>   "credentials": null,
>   "name": "xch"
> }
> ```
>
> *password*以及*credentials*等敏感信息已经被SpringSecurity屏蔽

- ##### 配置 继承了`WebSecurityConfigurerAdapter` 的配置类

```java
@Configuration
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {

    //注入自定义成功处理机制
    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
//                .loginPage("/login.html") //配置登录页面请求URL
                .loginPage("/authentication/require")
                .loginProcessingUrl("/login") //对应页面表单 form 的action=“/login"
                .successHandler(authenticationSuccessHandler) //配置成功处理器
                .and()
                .authorizeRequests() //授权配置
                .antMatchers("/authentication/require", "/login.html").permitAll() // 跳转 /login.html 请求不会被拦截
                .anyRequest() //所有请求
                .authenticated() //都需要认证
                .and().csrf().disable();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
```

> #### 小结
>
> `loginPage()`里配置的url，访问就是没有在antMatchers中配置过的路径，所要跳转的地址（无授权跳转地址）
>
> `LoginPorcessingUrl` 就是表单提交的接口 这里配置的是 `/login`; 也就是登录访问接口为`/login`且为 post时 是登录的接口。
>
> `antMatchers()`不需要授权的地址，资源

##### 重定向页面



- #### `MyAuthenticationSuccessHandler`中设置重定向

```java
@Component//这个类需要注入配置类，所以放入容器
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private ObjectMapper mapper;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        redirectStrategy.sendRedirect(request, response, "/index");
    }
}
```

- #### 处理`/index`请求

  **方式一**

  ```java
      @GetMapping("index")
      public Object index() {
          return SecurityContextHolder.getContext().getAuthentication();
      }
  ```

  **方式二**

  ```java
  	@GetMapping("index")
      public Object index(Authentication authentication) {
          return authentication;
      }
  ```

  这里 成功后都打印`Autentication`信息

