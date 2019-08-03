<%@ include file="includes/header.jsp" %>

<div id="infoGreen">As admin you can see and correct this info:</div>
<c:if test="${users != null}">
    <div id="info">All users:</div><br>
    <table id="cart" border=1>
        <tr id="tableTitle">
            <td>Login</td>
            <td>Name</td>
            <td>Address</td>
        </tr>

        <c:forEach var="user" items="${users}">
            <tr>
                <td><c:out value="${user.login}"/></td>
                <td><c:out value="${user.name}"/></td>
                <td><c:out value="${user.address}"/></td>
            </tr>
        </c:forEach>
    </table>
</c:if>
<br><br>

<c:if test="${products != null}">
    <div id="info">All products:</div>
    <div id="info"><a href="adminNewProduct">Add new product</a></div>
    <table id="myTable" border=1>
        <tr id="tableTitle">
            <td>Name</td>
            <td>Category</td>
            <td>Price</td>
            <td>Image</td>
            <td></td>
        </tr>

        <c:forEach var="product" items="${products}">
            <tr>
                <td><c:out value="${product.name}"/></td>
                <td><c:out value="${product.category}"/></td>
                <td><c:out value="${product.price}"/></td>
                <td><c:out value="${product.image}"/></td>
                <td><button onclick="deleteProduct('${product.id}')">delete</button></td>
            </tr>
        </c:forEach>
    </table>
</c:if>

<%@ include file="includes/footer.jsp" %>
