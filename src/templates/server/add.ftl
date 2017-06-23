<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>Add Server</title>
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
                <li class="active"><i class="fa fa-globe"></i> ${location}</a></li>
            </ol>
        </section>

        <!-- Main content -->
        <section class="content">
            <div class="row">
                <div class="col-xs-6">

                    <div class="box">
                        <div class="box-header">
                            <h3 class="box-title">Add Server</h3>
                        </div>
                        <form action="/server/add" method="post" role="form">

                            <div class="box-body">

                                <div class="form-group">
                                    <label for="name">Name</label>
                                    <input id="name" name="name" type="text" maxlength="15" class="form-control" placeholder="Server's name">

                                    <label for="host">Host</label>
                                    <input id="host" name="host" type="text" maxlength="40" class="form-control" placeholder="Server's host">

                                    <label for="description">Description</label>
                                    <input id="description" name="description" type="text" maxlength="50" class="form-control"
                                           placeholder="Server's description">
                                </div>

                            </div>
                            <!-- /.box-body -->
                            <div class="box-footer">
                                <button type="submit" class="btn btn-primary" >Submit</button>
                            </div>
                        </form>
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
