<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>Server</title>
<#include "../head.ftl">
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
<#include "../header.ftl">

<#include "../sidebar.ftl">
<!-- Content Wrapper. Contains page content -->
<div class="content-wrapper">
    <!-- Content Header (Page header) -->
    <section class="content-header">
        <h1>
            ${entity?cap_first}
        </h1>
        <ol class="breadcrumb">
            <li><a href="/"><i class="fa fa-dashboard"></i> Home</a></li>
            <li><a href="/server/${server}"><i class="fa fa-globe"></i> ${server}</a></li>
            <li><a href="/server/${server}/entity/${entity}"><i class="fa fa-dashboard"></i> ${entity}</a></li>
            <li class="active"><i class="fa fa-cube"></i> ${entity?cap_first}</li>
        </ol>
    </section>

    <!-- Main content -->
    <section class="content">
        <div class="row">


            <div class="col-md-6">
                <!-- general form elements -->
                <div class="box box-primary">
                    <div class="box-header with-border">
                        <h3 class="box-title">Edit</h3>
                    </div>
                    <!-- /.box-header -->
                    <!-- form start -->
                    <form role="form">

                        <div class="box-body">

                            <div class="form-group">
                                <label for="inputID">id</label>

                                <input type="text" class="form-control" disabled="true" id="inputID"
                                       value="${instance.id}" placeholder="id">

                            </div>

                        <#list attributes as attribute>
                        <div class="form-group">
                        <label for="input${attribute.name}">${attribute.name}</label>
                            <#if attribute.type?lower_case == "boolean">
                                <div style="display: block">
                                    <input id="input${attribute.name}" type="checkbox" class="minimal" <#if instance[attribute.name]?? && instance[attribute.name] == "true" >checked</#if>>
                                </div>
                            <#else>
                        <input type="text" class="form-control" id="input${attribute.name}" value="<#if instance[attribute.name]?? >${instance[attribute.name]}</#if>" placeholder="${attribute.name}">
                            </#if>
                            </div>
                        </#list>

                        </div>
                        <!-- /.box-body -->

                        <div class="box-footer">
                            <button type="submit" class="btn btn-primary" onclick="update()">Update</button>
                        </div>
                    </form>
                </div>
                <!-- /.box -->

            </div>


        </div>
        <!-- /.row -->
    </section>

</div>

<#include "../footer.ftl">
<#include "../control.ftl">

</div>
<#include "../scripts.ftl">
<script>
    function update(){
        var idValue = $("#inputID").val();
    <#list attributes as attribute>
        <#if attribute.type?lower_case == "boolean">
        var ${attribute.name}Value = $("#input${attribute.name}").is(':checked');
        <#elseif attribute.type?lower_case == "integer">
        var ${attribute.name}Value = parseInt($("#input${attribute.name}").val());
        <#else>
        var ${attribute.name}Value = $("#input${attribute.name}").val();
        </#if>
    </#list>

        $.ajax({
            url: "${host}/api/entity/${entity}/instance",
            type: 'PUT',
            dataType: "json",
            traditional: true,
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify({
                id: idValue,
            <#list attributes as attribute>
                ${attribute.name}: ${attribute.name}Value,
            </#list>
            }),
        success: function(result) {
            //todo alert
            location.reload();
        },
        failure: function(result) {
            //todo alert
            console.log(result)
        }
    });
    }
    $('input[type="checkbox"].minimal, input[type="radio"].minimal').iCheck({
        checkboxClass: 'icheckbox_minimal-blue',
        radioClass: 'iradio_minimal-blue'
    });

    <#list attributes as attribute>
    <#if attribute.type?lower_case == "date">
    $('#input${attribute.name}').datepicker({
        autoclose: true
    });
    </#if>
    </#list>


</script>
</body>
</html>
