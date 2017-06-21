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
            Instance
        </h1>
        <ol class="breadcrumb">
            <li><a href="/"><i class="fa fa-dashboard"></i> Home</a></li>
            <li><a href="/"><i class="fa fa-dashboard"></i> todo</a></li>
            <li class="active"><i class="fa fa-dashboard"></i> Instance</li>
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
                        <label for="input3">${attribute.name}</label>
                        <input type="text" class="form-control" id="input3" value="${instance[attribute.name]}" placeholder="${attribute.name}">
                        </div>
                        </#list>

                        </div>
                        <!-- /.box-body -->

                        <div class="box-footer">
                            <button type="submit" class="btn btn-primary">Submit</button>
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
</body>
</html>
