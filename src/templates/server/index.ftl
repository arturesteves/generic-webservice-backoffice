<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>Home</title>
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
                Home
            </h1>
            <ol class="breadcrumb">
                <li class="active"><i class="fa fa-dashboard"></i> Home</a></li>
            </ol>
        </section>

        <!-- Main content -->
        <section class="content">
            <div class="row">
                <div class="col-xs-12">

                    <div class="box">
                        <div class="box-header">
                            <h3 class="box-title">Servers</h3>
                        </div>
                        <!-- /.box-header -->
                        <div class="box-body">

                            <#list servers as server>
                            <div class="col-md-3 col-sm-6 col-xs-12">
                                <div class="info-box">
                                    <a href="/server/${server.name}"><span class="info-box-icon bg-aqua"><i class="fa fa-server"></i></span></a>

                                    <div class="info-box-content">
                                        <span class="info-box-text">Server ${server.name}</span>
                                        <span style="color: rgba(32, 32, 32, 0.76);">${server.description}</span>
                                    </div>
                                    <!-- /.info-box-content -->
                                </div>
                                <!-- /.info-box -->
                            </div>
                            </#list>
                        </div>
                        <!-- /.box-body -->
                        <div class="box-footer">
                            <a class="btn  btn-primary" href="/server/add"">Add new</a>
                        </div>
                    </div>
                    <!-- /.box -->
                </div>
                <!-- /.col -->
            </div>
            <!-- /.row -->
        </section>
        <!-- /.content -->
    </div>
    <!-- /.content-wrapper -->


<#include "../footer.ftl">
<#include "../control.ftl">

</div>
<#include "../scripts.ftl">
</body>
</html>