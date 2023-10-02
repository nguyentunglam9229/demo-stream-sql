package com.example.demostreamsqldata.web.rest;

import com.example.demostreamsqldata.entity.User;
import com.example.demostreamsqldata.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpHeaders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Stream;

import static com.example.demostreamsqldata.util.LambdaExceptionUtil.rethrowConsumer;

@RestController
@RequestMapping(value = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserResource {

    private final UserRepository userRepository;

    @PersistenceContext
    EntityManager entityManager;

    @GetMapping(value = "/export")
    @Transactional(readOnly = true)
    public void exportCsv(HttpServletResponse response) throws IOException {
        log.info("start export");
        response.addHeader(HttpHeaders.CONTENT_TYPE, "application/csv");
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=todos.csv");
        response.setCharacterEncoding("UTF-8");
        try(Stream<User> userStream = userRepository.streamAll()) {
            PrintWriter out = response.getWriter();
            userStream.forEach(rethrowConsumer(user -> {
                String line = userToCsv(user);
                out.write(line);
                out.write("\n");
                entityManager.detach(user);
            }));
            out.flush();
        } catch (IOException e) {
            log.info("Exception occurred " + e.getMessage(), e);
            throw new RuntimeException("Exception occurred while exporting results", e);
        }
        log.info("end export");

    }

    @GetMapping(value = "/export-page")
    @Transactional(readOnly = true)
    public void exportTodosCSVSlicing(HttpServletResponse response) {
        final int PAGE_SIZE = 1000;
        log.info("start export");
        response.addHeader("Content-Type", "application/csv");
        response.addHeader("Content-Disposition", "attachment; filename=todos.csv");
        response.setCharacterEncoding("UTF-8");
        try {
            PrintWriter out = response.getWriter();
            int page = 0;
            Slice<User> userPage;
            do {
                userPage = userRepository.findAllBy(PageRequest.of(page, PAGE_SIZE));
                for (User user : userPage) {
                    String line = userToCsv(user);
                    out.write(line);
                    out.write("\n");
                }
                entityManager.clear();
                page++;
            } while (userPage.hasNext());
            out.flush();
        } catch (IOException e) {
            log.info("Exception occurred " + e.getMessage(), e);
            throw new RuntimeException("Exception occurred while exporting results", e);
        }
        log.info("end export");

    }

    private String userToCsv(User user) {
        return String.join(",", "" + user.getId(), "" + user.getUsername(), "" + user.getEmail());
    }
}
