package com.example.demo;

import com.example.demo.db.data.User;
import com.example.demo.db.data.User1;
import com.example.demo.db.data.User2;
import com.example.demo.db.service.SequenceGeneratorService;
import com.example.demo.db.service.TestService;
import com.example.demo.db.service.user.mongo.MongoUserService;
import com.example.demo.db.service.user.mongo.UserRepository;
import com.example.demo.helper.ReflectionUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.mongodb.BasicDBObject;
import com.mongodb.QueryBuilder;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {
    @Autowired
    private MongoUserService userDao;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestService testService;
    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;
    @Autowired
    private MongoTemplate mongoTemplate;

    private static final ObjectMapper OBJECTMAPPER = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    static {
        OBJECTMAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        OBJECTMAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        OBJECTMAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        OBJECTMAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
    }

    @Test
    public void contextLoads() {
    }

    @Test
    public void saveDemoTest() {

        User1 demoEntity1 = new User1();
        demoEntity1.setId(1L);
        demoEntity1.setTitle("Spring Boot");
        demoEntity1.setDescription("关注公众号，搜云库，专注于开发技术的研究与知识分享");
        demoEntity1.setBy("souyunku");
        demoEntity1.setUrl("http://www.souyunku.com");
        userDao.save1(demoEntity1);

        User2 demoEntity2 = new User2();
        demoEntity2.setId(2L);
        demoEntity2.setTitle("Spring Boot 中使用 MongoDB");
        demoEntity2.setDescription("关注公众号，搜云库，专注于开发技术的研究与知识分享");
        demoEntity2.setBy("souyunku");
        demoEntity2.setUrl("http://www.souyunku.com");
        demoEntity2.setHeadImg("http://www.1234567.com/239.png");
        userDao.save2(demoEntity2);
    }

    @Test
    public void findDemoByIdTest() throws JsonProcessingException {
        Map map = userDao.findById(1L);
        System.out.println("map1->" + OBJECTMAPPER.writeValueAsString(map));
        map = userDao.findById(2L);
        System.out.println("map2->" + OBJECTMAPPER.writeValueAsString(map));

        User1 user1 = userDao.findUser1ById(1L);
        System.out.println("user1->" + OBJECTMAPPER.writeValueAsString(user1));
        User2 user2 = userDao.findUser2ById(2L);
        System.out.println("user2->" + OBJECTMAPPER.writeValueAsString(user2));
    }

    @Test
    public void testChangeUser1() throws IOException, NotFoundException, IllegalAccessException, InstantiationException, ClassNotFoundException, CannotCompileException {
        this.saveDemoTest();
        // 获取数据，并动态创建新实体类
        Map<String, Object> map = userDao.findById(2L);
        Object user1 = new ReflectionUtils<>(User1.class).builder(map).writerFileds(null);
        System.out.println("user1->" + OBJECTMAPPER.writeValueAsString(user1));

//        // 处理新的实体类，修改值，插入新记录
//        map.remove("_id");
//        map.put("title", "new object");
//        FieldUtils.writeField(user1, "id", null, true);
//        Object user2 = new ReflectionUtils<>(User1.class).builder(map).writerFileds(null);
//        FieldUtils.writeField(user2, "id", sequenceGeneratorService.generateSequence(User1.SEQUENCE_NAME), true);
//        userDao.save(user2);
//        System.out.println("user2->" + OBJECTMAPPER.writeValueAsString(user2));

        // 通过原始实体类，获取添加新属性的实体类
        User1 user3 = userDao.findUser1ById(1L);
        System.out.println("user1_new ->" + OBJECTMAPPER.writeValueAsString(user3));
        map = new HashMap<>();
        map.put("headImg", "http://192.168.1.123/index.html");
        Object user4 = new ReflectionUtils<>(user3).getChangedObject(null, map);
        userDao.save(user4);
        System.out.println("user1_changed ->" + OBJECTMAPPER.writeValueAsString(user4));


        // 在已有新实体类的基础上再次添加新的属性
        map = userDao.findById(2L);
        map.put("headImg", "http://new_id/index.html");
        map.put("new_id", 12345);
        Object user5 = new ReflectionUtils<>(User1.class).builder(map).writerFileds(null);
        userDao.save(user5);
        System.out.println("user5->" + OBJECTMAPPER.writeValueAsString(user5));

        for (int i = 0; i < 10; i++) {
            // 在已有新实体类的基础上再次添加新的属性 10次
            map = userDao.findById(2L);
            map.put("headImg", "http://" + i + "/index.html");
            map.put("new_id" + i, i * i);
            Object user = new ReflectionUtils<>(User1.class).builder(map).writerFileds(null);
            userDao.save(user);
            System.out.println("user6[" + i + "]->" + OBJECTMAPPER.writeValueAsString(user));
        }
    }

    @Test
    public void saveUserByRepository() throws JsonProcessingException, InvocationTargetException, IllegalAccessException {
        User demoEntity1 = new User();
//        demoEntity1.setId(sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME));
        demoEntity1.setId(3L);
        demoEntity1.setTitle("Spring中文1");
        demoEntity1.setDescription("关注公众号，搜云库，专注于开发技术的研究与知识分享");
        demoEntity1.setBy("souyunku");
        demoEntity1.setUrl("http://www.souyunku.com");
        userRepository.save(demoEntity1);

        User demoEntity2 = new User();
//        demoEntity2.setId(sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME));
        demoEntity2.setId(4L);
        demoEntity2.setTitle("Spring中文2");
        demoEntity2.setDescription("关注公众号，搜云库，专注于开发技术的研究与知识分享");
        demoEntity2.setBy("souyunku");
        demoEntity2.setUrl("http://www.souyunku.com");
        demoEntity2.addDetails("headImg", "http://www.1234567.com/239.png")
                .addDetails("test2", 123)
                .addDetails("name", "test2")
                .addDetails("password", "test2")
                .addDetails("list", Arrays.asList("1", "2", "3"));
        userRepository.save(demoEntity2);

        User user1 = userRepository.findOne(1L);
        System.out.println("user1.findOne(1L)->" + OBJECTMAPPER.writeValueAsString(user1));

        User user2 = userRepository.findOne(2L);
        System.out.println("user2.findOne(2L)->" + OBJECTMAPPER.writeValueAsString(user2));

        List<User> users = userRepository.findByDynamicField("details.name", "test2");
        System.out.println("users.findByDynamicField(\"details.name\", \"test2\")->" + OBJECTMAPPER.writeValueAsString(users));

        users = userDao.findUserByName("test2");
        System.out.println("users.findUserByName(\"test2\")->" + OBJECTMAPPER.writeValueAsString(users));

        List<User> users3 = userRepository.findByNameAndPassword("test2", "test2");
        System.out.println("根据自定义方法名查询  users.findByNameAndPassword(\"test2\", \"test2\")->" + OBJECTMAPPER
                .writeValueAsString(users3));

        Sort sort = new Sort(Sort.Direction.DESC, "id");
        //多个排序
        //sort.and()
        List<User> users4 = userRepository.findByTitleLike("Spring", sort);
        System.out.println("根据自定义方法名查询，并排序  users.findByNameAndPassword(\"test2\", \"test2\")->" + OBJECTMAPPER
                .writeValueAsString(users4));

        Pageable pageable = new PageRequest(1, 2, Sort.Direction.ASC, "id");
        Page<User> page = userRepository.findByTitleLike("Spring", pageable );
        System.out.println("根据自定义方法查询分页  ->" + OBJECTMAPPER
                .writeValueAsString(page));

    }

    @Test
    public void TestThrowableTransactional() {
        // 在用一个事务方法中，如果mysql先出错回滚了，MongoDB执行的代码就不会执行了
        testService.Error2SaveUser1();
    }

    @Test
    public void TestThrowableTransactional2() {
        // 该方法会造成MongoDB脏数据
        testService.Error2SaveUser2();
    }

    @Test
    public void getListWithBasicQuery() throws JsonProcessingException {
        User user = new User();

        user.setTitle("Spring");
        Pageable pageable =  new PageRequest(1, 2, Sort.Direction.ASC, "id");

        QueryBuilder queryBuilder = new QueryBuilder();

        //动态拼接查询条件
        if (!StringUtils.isEmpty(user.getTitle())) {
            Pattern pattern = Pattern.compile("^.*" + user.getTitle() + ".*$", Pattern.CASE_INSENSITIVE);
            queryBuilder.and("title").regex(pattern);
        }

        //if (user.getSex() != null) {
        //    queryBuilder.and("sex").is(user.getSex());
        //}
        //if (user.getCreateTime() != null) {
        //    queryBuilder.and("createTime").lessThanEquals(user.getCreateTime());
        //}

        Query query = new BasicQuery(queryBuilder.get().toString());
        //计算总数
        long total = mongoTemplate.count(query, User.class);

        //查询结果集条件
        BasicDBObject fieldsObject = new BasicDBObject();
        //id默认有值，可不指定
        fieldsObject.append("id", 1)    //1查询，返回数据中有值；0不查询，无值
                .append("title", 1);
        query = new BasicQuery(queryBuilder.get().toString(), fieldsObject.toJson());

        //查询结果集
        List<User> userList = mongoTemplate.find(query.with(pageable), User.class);
        Page<User> userPage = new PageImpl(userList, pageable, total);
        System.out.println("根据自定义方法查询分页  ->" + OBJECTMAPPER
                .writeValueAsString(userPage));
    }

    @Test
    public void TestSaveUserRef() {
        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setId(sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME));
        user1.setTitle("父");
        users.add(user1);
        User user2 = new User();
        user2.setId(sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME));
        user2.setTitle("母");
        users.add(user2);
        User user3 = new User();
        user3.setId(sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME));
        user3.setTitle("子");
        user3.setParents(Arrays.asList(user1, user2));
        users.add(user3);
        this.userRepository.save(users);
    }

    @Test
    public void TestFindAllUser() throws JsonProcessingException {
        List<User> users0 = userRepository.findByDynamicField("details.test2", 123);
        System.out.println("users0->\r\n" + OBJECTMAPPER.writeValueAsString(users0));
        User user = new User();
        user.setDetails(ImmutableMap.of("test2", 123));
        Example<User> example = Example.of(user, ExampleMatcher.matching()
                .withMatcher("details.test2", ExampleMatcher.GenericPropertyMatchers.exact()) // 精确查询
                .withIgnoreNullValues().withIgnorePaths("id"));

        Page<User> users = userRepository.findAllPage(example, new BasicDBObject(), new PageRequest(0, 10));
        System.out.println("users->\r\n" + OBJECTMAPPER.writeValueAsString(users));
    }
}
