package com.nowcoder.toutiao;

import com.nowcoder.toutiao.dao.MessageDAO;
import com.nowcoder.toutiao.dao.QuestionDAO;
import com.nowcoder.toutiao.dao.UserDAO;
import com.nowcoder.toutiao.model.EntityType;
import com.nowcoder.toutiao.model.Question;
import com.nowcoder.toutiao.model.User;
import com.nowcoder.toutiao.service.FollowService;
import com.nowcoder.toutiao.util.JedisAdapter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
@Sql("/init-schema.sql")
public class InitDatabaseTests {
	@Autowired
	UserDAO userDAO;

	@Autowired
	QuestionDAO questionDAO;

	@Autowired
	MessageDAO messageDAO;

	@Autowired
	FollowService followService;

	@Autowired
	JedisAdapter jedisAdapter;

	@Test
	public void initDatabase() {
		Random random=new Random();
		for (int i = 0; i < 11; i++) {
			User user=new User();
			user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",random.nextInt(1000)));
			user.setName(String.format("USER%d",i));
			user.setPassword("");
			user.setSalt("");
			userDAO.addUser(user);
			user.setPassword("sad");
			userDAO.updatePassword(user);

			for (int j = 1; j < i; j++) {
				followService.follow(j, EntityType.ENTITY_USER, i+1);
			}

			Question question=new Question();
			question.setCommentCount(i);
			Date date=new Date();
			date.setTime(date.getTime()+1000*3600*i);
			question.setCreatedDate(date);
			question.setUserId(i+1);
			question.setTitle(String.format("TITLE{%d}",i));
			question.setContent(String.format("adssad Content %d",i));

			questionDAO.addQuestion(question);

		}
		Assert.assertEquals("sad",userDAO.selectById(1).getPassword());
//		userDAO.deleteById(1);
//		Assert.assertNull(userDAO.selectById(1));
	}

}
