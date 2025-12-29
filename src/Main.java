
// Импорты
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import jakarta.persistence.*;
import javax.persistence.EntityManagerFactory;

// ТЕСТОВЫЙ КОНТРОЛЛЕР
@RestController
class TestController {
    @GetMapping("/")
    public String home() {
        return """
               <!DOCTYPE html>
               <html>
               <head>
                   <title>Hogwarts School API</title>
                   <style>
                       body { font-family: Arial, sans-serif; margin: 40px; }
                       h1 { color: #2c3e50; }
                       .endpoint { background: #f8f9fa; padding: 10px; margin: 10px 0; border-left: 4px solid #3498db; }
                       code { background: #ecf0f1; padding: 2px 4px; }
                   </style>
               </head>
               <body>
                   <h1>🎓 Hogwarts School REST API</h1>
                   <p>База данных: PostgreSQL (hogwarts)</p>
                   <p>Пользователь: student / chocolatefrog</p>
                   
                   <h2>📚 Доступные эндпоинты:</h2>
                   
                   <div class="endpoint">
                       <strong>Студенты:</strong>
                       <ul>
                           <li><code>GET /student</code> - все студенты</li>
                           <li><code>POST /student</code> - создать студента</li>
                           <li><code>GET /student/{id}</code> - получить студента по ID</li>
                           <li><code>PUT /student/{id}</code> - обновить студента</li>
                           <li><code>DELETE /student/{id}</code> - удалить студента</li>
                           <li><code>GET /student/age/{age}</code> - студенты по возрасту</li>
                           <li><code>GET /student/age-between?min=X&max=Y</code> - студенты в диапазоне возраста</li>
                       </ul>
                   </div>
                   
                   <div class="endpoint">
                       <strong>Факультеты:</strong>
                       <ul>
                           <li><code>GET /faculty</code> - все факультеты</li>
                           <li><code>POST /faculty</code> - создать факультет</li>
                           <li><code>GET /faculty/{id}</code> - получить факультет по ID</li>
                           <li><code>PUT /faculty/{id}</code> - обновить факультет</li>
                           <li><code>DELETE /faculty/{id}</code> - удалить факультет</li>
                           <li><code>GET /faculty/color/{color}</code> - факультеты по цвету</li>
                           <li><code>GET /faculty/search?name=X&color=Y</code> - поиск по имени или цвету</li>
                           <li><code>GET /faculty/{id}/students</code> - студенты факультета</li>
                       </ul>
                   </div>
                   
                   <h2>🛠 Примеры запросов:</h2>
                   <pre>
// Создать студента
POST /student
Content-Type: application/json
{
    "name": "Harry Potter",
    "age": 17
}

// Создать факультет
POST /faculty
Content-Type: application/json
{
    "name": "Gryffindor",
    "color": "red"
}
                   </pre>
                   
                   <p>🚀 <strong>Статус:</strong> Приложение работает!</p>
               </body>
               </html>
               """;
    }

    @GetMapping("/test-db")
    public String testDb() {
        return """
               {
                 "status": "OK",
                 "database": "hogwarts",
                 "user": "student",
                 "message": "Проверьте подключение к PostgreSQL"
               }
               """;
    }
}

