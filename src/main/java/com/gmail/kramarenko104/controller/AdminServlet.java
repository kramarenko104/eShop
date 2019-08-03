package com.gmail.kramarenko104.controller;

import com.gmail.kramarenko104.dao.ProductDao;
import com.gmail.kramarenko104.dao.UserDao;
import com.gmail.kramarenko104.factoryDao.DaoFactory;
import com.gmail.kramarenko104.model.Product;
import com.gmail.kramarenko104.model.User;
import org.apache.log4j.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminServlet", urlPatterns = {"/admin", "/adminNewProduct"})
public class AdminServlet extends HttpServlet {

    private static Logger logger = Logger.getLogger(LoginServlet.class);
    private static final String DB_WARNING = "Check your connection to DB!";
    private DaoFactory daoFactory;

    public AdminServlet() {
        daoFactory = DaoFactory.getSpecificDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        if (session != null) {
            if (daoFactory.openConnection()) {
                if (req.getRequestURI().endsWith("/adminNewProduct")){
                    req.getRequestDispatcher("WEB-INF/view/adminNewProduct.jsp").forward(req, resp);
                } else {
                    UserDao userDao = daoFactory.getUserDao();
                    List<User> usersList = userDao.getAllUsers();
                    session.setAttribute("usersList", usersList);
                    daoFactory.deleteUserDao(userDao);

                    ProductDao productDao = daoFactory.getProductDao();
                    List<Product> productsList = productDao.getAllProducts();
                    session.setAttribute("productsList", productsList);
                    daoFactory.deleteProductDao(productDao);
                    req.getRequestDispatcher("WEB-INF/view/admin.jsp").forward(req, resp);
                }
                daoFactory.closeConnection();
            } else {
                session.setAttribute("warning", DB_WARNING);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        if (session != null) {
            if (daoFactory.openConnection()) {
                ProductDao productDao = daoFactory.getProductDao();

                String param = req.getParameter("action");
                if (param != null) {
                    switch (param) {
                        case "addProduct":
                            Product newProduct = new Product();
                            newProduct.setName(req.getParameter("name"));
                            newProduct.setPrice(Integer.valueOf(req.getParameter("price")));
                            String category = req.getParameter("category");
                            int categoryInt = ("dress".equals(category) ? 1 : ("shoes".equals(category) ? 2 : 3));
                            newProduct.setCategory(categoryInt);
                            newProduct.setDescription(req.getParameter("description"));
                            newProduct.setImage(req.getParameter("image"));
                            productDao.addProduct(newProduct);
                            break;
                        case "deleteProduct":
                            productDao.deleteProduct(Integer.valueOf(req.getParameter("productId")));
                    }
                }
                daoFactory.deleteProductDao(productDao);
            }
            daoFactory.closeConnection();
        } else {
            session.setAttribute("warning", DB_WARNING);
        }
        resp.sendRedirect("./admin");
    }
}
