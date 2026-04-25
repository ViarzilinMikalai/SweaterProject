<#include "parts/security.ftl">
<#import "parts/common.ftl" as c>

<@c.page>
<h1>Hello, <#if user??>${user.getUsername()}<#else>guest</#if>!</h1>
<div>It's a simple clone of Twitter</div>
</@c.page>
