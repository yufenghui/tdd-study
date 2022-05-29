package com.yufenghui.tdd.springboot;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.vchangyi.boot.common.response.PageResponse;
import com.yufenghui.tdd.springboot.model.User;
import com.yufenghui.tdd.springboot.request.UserPageRequest;
import com.yufenghui.tdd.springboot.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UserServiceTest
 * <p/>
 *
 * @author yufenghui
 * @date 2022/5/23 17:18
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserServiceTest {

    @Autowired
    private ApplicationContext applicationContext;

    private UserService userService;


    @BeforeEach
    public void init_user_service() {
        userService = applicationContext.getBean(UserService.class);
        assertNotNull(userService);
    }

    @Nested
    public class UserQuery {

        @BeforeEach
        public void init_database() {
            User user1 = User.builder().name("yfh1").age(18)
                    .createTime(LocalDateTimeUtil.now()).updateTime(LocalDateTimeUtil.now()).build();
            userService.save(user1);
            User user2 = User.builder().name("yfh2").age(18)
                    .createTime(LocalDateTimeUtil.now()).updateTime(LocalDateTimeUtil.now()).build();
            userService.save(user2);
            User user3 = User.builder().name("yfh3").age(18)
                    .createTime(LocalDateTimeUtil.now()).updateTime(LocalDateTimeUtil.now()).build();
            userService.save(user3);
            User user4 = User.builder().name("yfh4").age(18)
                    .createTime(LocalDateTimeUtil.now()).updateTime(LocalDateTimeUtil.now()).build();
            userService.save(user4);
        }

        @AfterEach
        public void clear_database() {
            userService.remove(null);
        }

        @Test
        public void test_page_user_with_query() {
            UserPageRequest request = new UserPageRequest();
            request.setPageNum(1);
            request.setPageSize(2);
            request.setName("yfh");

            PageResponse<User> pageResponse = userService.pageUser(request);
            List<User> userList = pageResponse.getRecords();

            assertNotNull(pageResponse);
            assertEquals(4, pageResponse.getTotal());
            assertEquals(2, userList.size());
            assertEquals("yfh1", userList.get(0).getName());
            assertEquals(18, userList.get(0).getAge());

        }

    }

    @Nested
    public class UserSave {

        @AfterEach
        public void clear_database() {
            userService.remove(null);
        }

        @Test
        public void test_save_user_to_database() {
            User user = User.builder()
                    .name("yfh1")
                    .age(18)
                    .createTime(LocalDateTimeUtil.now())
                    .updateTime(LocalDateTimeUtil.now())
                    .build();

            boolean result = userService.save(user);
            assertTrue(result);
            assertNotNull(user.getId());
        }

    }

}
