package cz.muni.fi.pv168.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cz.mumi.fi.pv168.backend.Guest;
import cz.mumi.fi.pv168.backend.GuestManager;
import cz.mumi.fi.pv168.backend.ServiceFailureException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Created by Filip on 23.4.14.
 */

    /**
     * Servlet for managing books.
     */
    @WebServlet(GuestServlet.URL_MAPPING + "/*")
    public class GuestServlet extends HttpServlet {

        private static final String LIST_JSP = "/list.jsp";
        public static final String URL_MAPPING = "/guests";

        private final static Logger log = LoggerFactory.getLogger(GuestServlet.class);

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            showGuestsList(request, response);
        }

        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            request.setCharacterEncoding("utf-8");

            String action = request.getPathInfo();
            String fullName;
            String passportNumber;
            String creditCardNumber;
            Boolean vipCustomer;

            switch (action) {
                case "/add":
                    fullName = request.getParameter("fullName2");
                    passportNumber = request.getParameter("passportNumber2");
                    creditCardNumber = request.getParameter("creditCardNumber2");
                    vipCustomer = request.getParameter("vipCustomer2") != null;

                    if (fullName == null || fullName.length() == 0
                            || passportNumber == null || passportNumber.length() == 0
                                || creditCardNumber == null || creditCardNumber.length() == 0) {
                        request.setAttribute("error", "All fields must be filled in!");
                        showGuestsList(request, response);
                        return;
                    }
                    try {
                        Guest guest = new Guest();
                        guest.setFullName(fullName);
                        guest.setPassportNumber(passportNumber);
                        guest.setCreditCardNumber(creditCardNumber);
                        guest.setVipCustomer(vipCustomer);

                        getGuestManager().createGuest(guest);
                        log.debug("created {}",guest);
                        response.sendRedirect(request.getContextPath()+URL_MAPPING);
                        return;
                    } catch (ServiceFailureException e) {
                        log.error("Cannot add guest", e);
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                        return;
                    }
                case "/delete":
                    try {
                        Long id = Long.valueOf(request.getParameter("id"));
                        Guest temp = getGuestManager().findGuestById(id);
                        getGuestManager().deleteGuest(temp);
                        log.debug("deleted guest {}",temp);
                        response.sendRedirect(request.getContextPath()+URL_MAPPING);
                        return;
                    } catch (ServiceFailureException e) {
                        log.error("Cannot delete guest", e);
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                        return;
                    }
                case "/edit":
                    fullName = request.getParameter("fullName");
                    passportNumber = request.getParameter("passportNumber");
                    creditCardNumber = request.getParameter("creditCardNumber");
                    vipCustomer = request.getParameter("vipCustomer") != null;

                    if (fullName == null || fullName.length() == 0
                            || passportNumber == null || passportNumber.length() == 0
                            || creditCardNumber == null || creditCardNumber.length() == 0) {
                        request.setAttribute("error", "All fields must be filled in!");
                        showGuestsList(request, response);
                        return;
                    }
                    try {
                        Long id = Long.valueOf(request.getParameter("id"));
                        Guest guest = getGuestManager().findGuestById(id);
                        guest.setFullName(fullName);
                        guest.setPassportNumber(passportNumber);
                        guest.setCreditCardNumber(creditCardNumber);
                        guest.setVipCustomer(vipCustomer);

                        getGuestManager().updateGuest(guest);
                        log.debug("created {}",guest);
                        response.sendRedirect(request.getContextPath()+URL_MAPPING);
                        return;
                    } catch (ServiceFailureException e) {
                        log.error("Cannot add guest", e);
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                        return;
                    }
                default:
                    log.error("Unknown action " + action);
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Unknown action " + action);
            }
        }

        private GuestManager getGuestManager() {
            return (GuestManager) getServletContext().getAttribute("guestManager");
        }

        private void showGuestsList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            try {
                request.setAttribute("guests", getGuestManager().findAllGuest());
                request.getRequestDispatcher(LIST_JSP).forward(request, response);
            } catch (ServiceFailureException e) {
                log.error("Cannot show guests", e);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }

    }
