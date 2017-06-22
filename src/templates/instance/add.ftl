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
                <li><a href="/server/${server}"><i class="fa fa-dashboard"></i>${server}</a></li>
                <li><a href="/server/${server}/entity/${entity}"><i class="fa fa-dashboard"></i>${entity}</a></li>
                <li class="active"><i class="fa fa-dashboard"></i> add</li>
            </ol>
        </section>

        <!-- Main content -->
        <section class="content">
            <div class="row">


                <div class="col-md-6">
                    <!-- general form elements -->
                    <div class="box box-primary">
                        <div class="box-header with-border">
                            <h3 class="box-title">Create</h3>
                        </div>
                        <!-- /.box-header -->
                        <!-- form start -->
                        <form role="form">

                            <div class="box-body">

                            <#list attributes as attribute>

                                <div class="form-group">

                                    <label for="input${attribute.name}">${attribute.name}</label>
                                    <#if attribute.type?lower_case == "boolean">
                                    <div style="display: block">
                                    <input id="input${attribute.name}" type="checkbox" class="minimal">
                                    </div>
                                    <#else>
                                    <input type="text" class="form-control" id="input${attribute.name}" value=""
                                           placeholder="${attribute.name}">
                                    </#if>
                                </div>
                            </#list>

                            </div>
                            <!-- /.box-body -->

                            <div class="box-footer">
                                <button type="submit" class="btn btn-primary" onclick="create()">Submit</button>
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
    function create() {
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
            type: 'POST',
            dataType: "json",
            traditional: true,
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify({
    <#list attributes as attribute>
    ${attribute.name}:
        ${attribute.name}Value,
    </#list>
    }),
        success: function (result) {

            location.reload();
        }
    })
        ;
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
