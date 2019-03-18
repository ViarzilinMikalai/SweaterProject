<#import "parts/common.ftl" as c>
<#import "parts/login.ftl" as l>

<@c.page>
<div>
    <@l.logout />
    <div><a href="/user">User list</a></div>
</div>
<div>
    <form method="post">
        <input type="text" name="text" placeholder="Введите сообщение" />
        <input type="text" name="tag" placeholder="Тэг">
        <input type="hidden" name="_csrf" value="${_csrf.token}" />
        <button type="submit">Добавить</button>
    </form>
</div>
<div>Список сообщений</div>
<form method="get" action="/main">
    <input type="text" name="filter" value="${filter}">
    <button type="submit">Найти</button>
</form>
    <#list messages as message>
<div>
    <table>
       <tr>
           <td>${message.id}</td>
           <td>${message.text}</td>
           <td>${message.tag}</td>
           <td>${message.authorName}</td>
       </tr>
    </table>

</div>

    </#list>
</@c.page>