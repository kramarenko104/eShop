package com.gmail.kramarenko104.controller;

import com.gmail.kramarenko104.dao.CartDao;
import com.gmail.kramarenko104.dao.OrderDao;
import com.gmail.kramarenko104.factoryDao.DaoFactory;
import com.gmail.kramarenko104.model.Cart;
import com.gmail.kramarenko104.model.Order;
import com.google.gson.Gson;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "OrderServlet", urlPatterns = {"/order"})
public class OrderServlet extends HttpServlet {

    private static Logger logger = Logger.getLogger(OrderServlet.class);
    private static final String DB_WARNING = "Check your connection to DB!";
    private DaoFactory daoFactory;

    public OrderServlet() {
        daoFactory = DaoFactory.getSpecificDao();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("WEB-INF/view/order.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();

        if (daoFactory.openConnection()) {
            if (session.getAttribute("user") != null) {
                // get info from Ajax POST request (updateCart.js)
                String param = req.getParameter("action");
                if (param != null && (param.equals("makeOrder"))) {
                    int userId = Integer.valueOf(req.getParameter("userId"));
                    logger.debug("OrderServlet.POST: got userId from POST request: " + userId);

                    // any user can have only one existing now cart and many processed orders (userId uniquely identifies cart)
                    CartDao cartDao = daoFactory.getCartDao();
                    Cart cart = cartDao.getCart(userId);
                    logger.debug("OrderServlet.POST: got cart from DB: " + cart);

                    // order will be created based on the cart's content
                    OrderDao orderDao = daoFactory.getOrderDao();
                    Order newOrder = orderDao.createOrder(userId, cart.getProducts());
                    logger.debug("OrderServlet.POST: !!! new Order was created: " + newOrder);
                    session.setAttribute("newOrder", newOrder);

                    // send JSON back with the new Order to show on order.jsp
                    if (newOrder != null) {
                        String jsondata = new Gson().toJson(newOrder);
                        logger.debug("OrderServlet: send JSON data to cart.jsp ---->" + jsondata);
                        try (PrintWriter out = resp.getWriter()) {
                            resp.setContentType("application/json");
                            resp.setCharacterEncoding("UTF-8");
                            out.print(jsondata);
                            out.flush();
                        }
                    }
                    cartDao.deleteCart(userId);
                    session.setAttribute("userCart", null);
                    daoFactory.deleteCartDao(cartDao);
                    daoFactory.deleteOrderDao(orderDao);
                }
            }
            daoFactory.closeConnection();
        } else {
            session.setAttribute("warning", DB_WARNING);
        }
    }
}
