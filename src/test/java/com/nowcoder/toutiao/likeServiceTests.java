package com.nowcoder.toutiao;

import com.nowcoder.toutiao.service.LikeService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author tktktkl@foxmail.com
 * @date 2019/6/20 13:47
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ToutiaoApplication.class)
//@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
public class likeServiceTests {
    @Autowired
    LikeService likeService;

    @Before
    //添加数据到数据库
    public void setUp(){
        System.out.println("setUp");

    }

    @After
    //删除数据库表等数据
    public void tearDown(){
        System.out.println("tearDown");
    }

    //单元测试环境初始化
    @BeforeClass
    public static void beforeClass(){
        System.out.println("beforeClass");
    }

    @AfterClass
    public static void afterClass(){
        System.out.println("afterClass");
    }
    @Test
    //单元测试
    public void  testLike(){
        System.out.println("testLike");
        likeService.like(123, 1, 1);
        //判断返回值是否为1
        Assert.assertEquals(1, likeService.getLikeStatus(123, 1, 1));

        System.out.println("testdisLike");
        likeService.disLike(123, 1, 1);
        Assert.assertEquals(-1, likeService.getLikeStatus(123, 1, 1));

    }

    @Test
    public void testxxx(){
        System.out.println("testxxx");
    }

    @Test(expected = IllegalArgumentException.class)
    //异常测试
    public void testExection(){
        System.out.println("testException");
        throw new IllegalArgumentException("wrong");
    }

}
