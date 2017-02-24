var mysql = require('mysql');
var express = require('express');
var bodyParser = require('body-parser');

/* 데이터베이스 연결 */
var client = mysql.createConnection({
    host: 'localhost',
    port: 3306,
    user: 'xxxx',
    password: 'xxxx',
    database: 'donately'
});

/* 서버 생성 */
var app = express();
app.use(bodyParser.urlencoded({
    extended: false
}));

/* 서버 실행 */
app.listen(52273, function () {
    console.log('server running at http://127.0.0.1:52273');
});

/* 테스트 */
app.get('/', function (request, response) {
    response.send('Hello');
});

/* 여러개 컨텐츠 가져오기 (좋아요 항목은 사용자 기준) */
app.get('/users/:user_id/contents', function (request, response) {
    client.query('select content.content_id as id, content.title as title, content.description1 as description, content.description2 as description2, content.image_url as pictureUrl, content.link_url as linkUrl, content.type as type, if(isnull(favorite.content_id), "false", "true") as isFavorite, content.goal as goal, (select sum(point) as current_point from history where content_id = content.content_id) as currentPoint from content left outer join (select * from favorite where user_id = ?) as favorite on content.content_id = favorite.content_id', [request.params.user_id], function (error, results) {
        if (error) {
            console.log(error);
        } else {
            response.send(results);
            console.log("get contents");
        }
    });
});

/* 타입으로 검색한 여러개 컨텐츠 가져오기 (좋아요 항목은 사용자 기준) */
app.get('/users/:user_id/type/:type/contents', function (request, response) {
    client.query('select content.content_id as id, content.title as title, content.description1 as description, content.description2 as description2, content.image_url as pictureUrl, content.link_url as linkUrl, content.type as type, if(isnull(favorite.content_id), "false", "true") as isFavorite, content.goal as goal, (select sum(point) as current_point from history where content_id = content.content_id) as currentPoint from content left outer join (select * from favorite where user_id = ?) as favorite on content.content_id = favorite.content_id where content.type = ?', [request.params.user_id, request.params.type], function (error, results) {
        if (error) {
            console.log(error);
        } else {
            response.send(results);
            console.log("get contents by type : " + request.params.type);
        }
    });
});

/* 사용자가 좋아요한 여러개 컨텐츠 */
app.get('/users/:user_id/favorite/contents', function (request, response) {
    client.query('select content.content_id as id, content.title as title, content.description1 as description, content.description2 as description2, content.image_url as pictureUrl, content.link_url as linkUrl, content.type as type, if(isnull(favorite.content_id), "false", "true") as isFavorite, content.goal as goal, (select sum(point) as current_point from history where content_id = content.content_id) as currentPoint from content, (select * from favorite where user_id = ?) as favorite where content.content_id = favorite.content_id', [request.params.user_id], function (error, results) {
        if (error) {
            console.log(error);
        } else {
            response.send(results);
            console.log("get favorite contents");
        }
    });
});

/* 1개 컨텐츠(좋아요 항목은 사용자 기준) */
app.get('/users/:user_id/contents/:content_id', function (request, response) {
    client.query('select content.content_id as id, content.title as title, content.description1 as description, content.description2 as description2, content.image_url as pictureUrl, content.link_url as linkUrl, content.type as type, if(isnull(favorite.content_id), "false", "true") as isFavorite, content.goal as goal, (select sum(point) as current_point from history where content_id = content.content_id) as currentPoint from content left outer join (select * from favorite where user_id = ?) as favorite on content.content_id = favorite.content_id where content.content_id = ?', [
        request.params.user_id, request.params.content_id
    ], function (error, results) {
        if (error) {
            console.log(error);
        } else {
            var content = results[0];
            response.send(content);
            console.log("get a content");
        }
    });
});

/* 사용자의 기부 기록 가져오기 */
app.get('/users/:user_id/histories', function (request, response) {
    client.query('select history.history_id as id, history.user_id as userId, history.content_id as contentId, history.advertisement_id as advertisementId, UNIX_TIMESTAMP(history.donate_date) as donateDate, if(history.isclicked,"true", "false") as isClicked, history.point as point, content.title as title from history, content where history.content_id = content.content_id and user_id= ? order by donate_date desc', [
        request.params.user_id
    ], function (error, results) {
        if (error) {
            console.log(error);
        } else {
            response.send(results);
            console.log("get histories");
        }
    });
});

/* 기부 기록 추가하기 */
app.post('/histories', function (request, response) {
    var body = request.body;
    client.query('INSERT INTO history (user_id, content_id, advertisement_id, donate_date, isclicked, point) VALUES (?, ?, ?, current_timestamp(), ? ,?)', [
        body.user_id, body.content_id, body.advertisement_id, body.isclicked, body.point
    ], function (error, results) {
        if (error) {
            console.log(error);
        } else {
            response.send(results);
            console.log("insert history");
        }
    });
});

/* 좋아요 누르기 */
app.post('/favorites', function (request, response) {
    var body = request.body;
    client.query('INSERT INTO favorite (user_id, content_id) VALUES (?, ?) ON DUPLICATE KEY UPDATE user_id=?, content_id=?', [
        body.user_id, body.content_id, body.user_id, body.content_id
    ], function (error, results) {
        if (error) {
            console.log(error);
        } else {
            response.send(results);
            console.log("insert/update favorite");
        }
    });
});

/* 좋아요 취소하기 */
app.delete('/users/:user_id/contents/:content_id/favorites', function (request, response) {
    client.query('delete from favorite where user_id = ? and content_id = ?', [
        request.params.user_id, request.params.content_id
    ], function (error, results) {
        if(error) {
            console.log(error);
        } else {
            response.send(results);
            console.log("delete favorite");
        }
    });
});

/* 사용자 추가하기 */
app.post('/users', function (request, response) {
    var body = request.body;
    client.query('insert into user (user_id, name, email, access_token, type, photo_url) values (?, ?, ?, ?, ?, ?) on duplicate key update user_id = ?, name = ?, email = ? , access_token = ?, type = ?, photo_url = ?', [
        body.user_id, body.name, body.email, body.access_token, body.type, body.photo_url, body.user_id, body.name, body.email, body.access_token, body.type, body.photo_url
    ], function (error, results) {
        if (error) {
            console.log(error);
        } else {
            response.send(results);
            console.log("insert/update user");
        }
    });
});

/* 사용자 조회하기 */
app.get('/users/:user_id', function (request, response) {
    client.query('select user_id as id, name as name, email as email, access_token as accessToken, type as type, photo_url as photoUrl from user where user_id = ?', [
            request.params.user_id        
        ],function (error, results) {
        if (error) {
            console.log(error);
        } else {
            var user = results[0];
            response.send(user);
            console.log("get user");
        }
    });
});

/* 광고 한개 조회하기 */
app.get('/advertisement/length/:ad_length', function (request, response) {
    client.query('SELECT advertisement_id as id, length as length, file_url as fileUrl, promotion_url as promotionUrl FROM advertisement where length = ?', [
            request.params.ad_length
        ],function (error, results) {
        if (error) {
            console.log(error);
        } else {
            var advertisement = results[0];
            response.send(advertisement);
            console.log("get a advertisement");
        }
    });
});