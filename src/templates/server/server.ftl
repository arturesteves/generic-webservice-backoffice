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
                Home
            </h1>
            <ol class="breadcrumb">
                <li><a href="/"><i class="fa fa-dashboard"></i> Home</a></li>
                <li class="active"<i class="fa fa-dashboard"></i> ${server}</a></li>
            </ol>
        </section>

        <!-- Main content -->
        <section class="content">
            <div class="row">
                <div class="col-xs-12">

                    <div class="box">
                        <div class="box-header">
                            <h3 class="box-title">Entities</h3>
                        </div>
                        <!-- /.box-header -->
                        <div class="box-body">
                        <#assign colors = ["bg-green","bg-red","bg-yellow","bg-aqua"]>
                        <#list entities as entity>
                            <#assign i = (entity?index + 1) % 4   >
                            <div class="col-md-3 col-sm-6 col-xs-12">
                                <div class="info-box">
                                    <span class="info-box-icon ${colors[i]}"><i class="fa fa-cube"></i></span>

                                    <div class="info-box-content">
                                        <span class="info-box-text">${entity.name}</span>
                                        <span class="info-box-number">${entity.instanceCount}</span>
                                        <a class="info-box-more" href="/server/${server}/entity/${entity.name?lower_case}">See More...</a>
                                    </div>
                                    <!-- /.info-box-content -->

                                </div>
                                <!-- /.info-box -->
                            </div>
                        </#list>
                        </div>
                        <!-- /.box-body -->
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
