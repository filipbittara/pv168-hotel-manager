
<%@page contentType="text/html;charset=utf-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>

<table border="1">
    <thead>
    <tr>
        <th>Full name</th>
        <th>Passport number</th>
        <th>Credit card number</th>
        <th>VIP customer</th>
    </tr>
    </thead>
    <c:forEach items="${guests}" var="guest">
        <tr>
            <form method="post" action="${pageContext.request.contextPath}/guests/edit?id=${guest.id}" style="margin-bottom: 0;">
            <td><input type="text" name="fullName" value="<c:out value='${guest.fullName}'/>"/></td>
            <td><input type="text" name="passportNumber" value="<c:out value='${guest.passportNumber}'/>"/></td>
            <td><input type="text" name="creditCardNumber" value="<c:out value='${guest.creditCardNumber}'/>"/></td>
            <td>
                <c:choose>
                    <c:when test="${guest.vipCustomer=='true'}">
                        <input type="checkbox" name="vipCustomer" value="<c:out value='${guest.vipCustomer}'/>" checked />
                    </c:when>
                    <c:otherwise>
                        <input type="checkbox" name="vipCustomer" value="<c:out value='${guest.vipCustomer}'  />" />
                    </c:otherwise>
                </c:choose>
            </td>
            <td><input type="submit" value="Edit"></td>
            </form>
            <td><form method="post" action="${pageContext.request.contextPath}/guests/delete?id=${guest.id}"
                      style="margin-bottom: 0;"><input type="submit" value="Delete"></form></td>
        </tr>
    </c:forEach>
</table>

<h2>Add guest</h2>
<c:if test="${not empty error}">
    <div style="border: solid 1px red; background-color: yellow; padding: 10px">
        <c:out value="${error}"/>
    </div>
</c:if>
<form action="${pageContext.request.contextPath}/guests/add" method="post">
    <table style="text-align:left;">
        <tr>
            <th>Full name:</th>
            <td><input type="text" name="fullName2" value="<c:out value='${param.fullName2}'/>"/></td>
        </tr>
        <tr>
            <th>Passport number:</th>
            <td><input type="text" name="passportNumber2" value="<c:out value='${param.passportNumber2}'/>"/></td>
        </tr>
        <tr>
            <th>Credit card number:</th>
            <td><input type="text" name="creditCardNumber2" value="<c:out value='${param.creditCardNumber2}'/>"/></td>
        </tr>
        <tr>
            <th>VIP customer:</th>
            <td>
            <c:choose>
                <c:when test="${param.vipCustomer2 != null}">
                    <input type="checkbox" name="vipCustomer2" value="<c:out value='${param.vipCustomer2}'/>" checked/>
                </c:when>
                <c:otherwise>
                    <input type="checkbox" name="vipCustomer2" value="<c:out value='${param.vipCustomer2}'/>"/>
                </c:otherwise>
            </c:choose>
            </td>
        </tr>
    </table>
    <input type="Submit" value="Save" />
</form>
</body>
</html>