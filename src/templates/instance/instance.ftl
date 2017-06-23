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
            <li><a href="/server/${server}"><i class="fa fa-dashboard"></i> ${server}</a></li>
            <li><a href="/server/${server}/entity/${entity}"><i class="fa fa-dashboard"></i> ${entity}</a></li>
            <li class="active"><i class="fa fa-dashboard"></i> ${entity?cap_first}</li>
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

                                <label for="input${attribute.name}">${(attribute.displayName??)?then(attribute.displayName, attribute.name)}</label>
                                <#switch attribute.type?lower_case>
                                    <#case "boolean">
                                        <div style="display: block">
                                            <input id="input${attribute.name}" type="checkbox" class="minimal" <#if instance[attribute.name] >checked</#if>>
                                        </div>
                                        <#break>
                                    <#case "association">
                                        <div class="form-group">
                                            <#if attribute.maxOccurs == "*">
                                            <select id="select${attribute.name}" class="form-control select2" multiple="multiple" data-placeholder="Select a ${attribute.entity}" style="width: 100%;">
                                            </select>
                                            <#else>
                                            <select id="select${attribute.name}" class="form-control select" data-placeholder="Select a ${attribute.entity}" style="width: 100%;">
                                            </select>
                                            </#if>
                                        </div>
                                        <#break>
                                    <#default>
                                        <input type="text" class="form-control" id="input${attribute.name}" value="${instance[attribute.name]}"
                                               placeholder="${attribute.name}">
                                </#switch>
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
        var idValue = parseInt($("#inputID").val());
    <#list attributes as attribute>
        <#if attribute.type?lower_case == "boolean">
            var ${attribute.name}Value = $("#input${attribute.name}").is(':checked');
        <#elseif attribute.type?lower_case == "integer" || attribute.type?lower_case == "int">
            var ${attribute.name}Value = parseInt($("#input${attribute.name}").val());
        <#elseif attribute.type?lower_case == "date">
            var ${attribute.name}Value = new Date($("#input${attribute.name}").val()).toISOString();
        <#elseif attribute.type?lower_case == "double">
            var ${attribute.name}Value = parseFloat($("#input${attribute.name}").val());
        <#elseif attribute.type?lower_case == "association">
            <#if attribute.maxOccurs == "*">
        var ${attribute.name}Value = $("#select${attribute.name}").select2("val") || [];
        ${attribute.name}Value = ${attribute.name}Value.map(function(v){ return parseInt(v);});
            <#else>
        var ${attribute.name}Value = parseInt($("#select${attribute.name}").val() || null);
            </#if>
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

    $(".select2").select2();
    $('input[type="checkbox"].minimal, input[type="radio"].minimal').iCheck({
        checkboxClass: 'icheckbox_minimal-blue',
        radioClass: 'iradio_minimal-blue'
    });


    <#list attributes as attribute>
        <#switch attribute.type?lower_case>
            <#case "date">
            $('#input${attribute.name}').datepicker({
                autoclose: true
            });
                <#break>
            <#case "association">
            $.ajax({
                url: "${host}/api/entity/${attribute.entity?lower_case}/instance",
                type: 'GET',
                traditional: true,
                contentType: "application/json; charset=utf-8",
                success: function (result) {
                    result.forEach(function(instance) {
                        $("#select${attribute.name}").append($('<option>', {
                            value: instance.id,
                            text: instance.displayName || JSON.stringify(instance)
                        }));
                    });
                    <#if attribute.maxOccurs == "*">
                    $("#select${attribute.name}").val([<#list instance[attribute.name] as id>
                        '${id}'<#sep>, </#sep>
                    </#list>]);
                    <#else>
                    $("#select${attribute.name}").val(${(instance[attribute.name]??)?then(instance[attribute.name],"")});
                    </#if>

                    console.log(result);
                }});

                <#break>
        </#switch>
    </#list>



</script>
</body>
</html>
