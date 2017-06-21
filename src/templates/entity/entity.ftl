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
                <div class="col-xs-12">

                    <div class="box">
                        <div class="box-header">
                            <h3 class="box-title">Author</h3>
                        </div>
                        <!-- /.box-header -->
                        <div class="box-body">
                            <table id="example2" class="table table-bordered table-striped">
                                <thead>
                                <tr>
                                    <th>id</th>
                                    <#list attributes as attribute>
                                    <th>${attribute.name}</th>
                                    </#list>
                                </tr>
                                </thead>
                                <tbody>
                                <#list instances as instance>
                                <tr>
                                    <td>${instance.id}</td>
                                    <#list attributes as attribute>
                                    <td>${instance[attribute.name]}</td>
                                    </#list>
                                    <#--<th>${author.id}</th>-->
                                    <#--<th>${author.tangible?string("yes", "no")}</th>-->
                                    <#--<th><#if author.firstName?? >author.firstName}</#if></th>-->
                                    <#--<th><#if author.lastName?? >author.lastName}</#if></th>-->
                                    <#--<th><#if author.email?? >author.email}</#if></th>-->
                                </tr>
                                </#list>
                                </tbody>
                                <tfoot>
                                <tr>
                                    <th>id</th>
                                <#list attributes as attribute>
                                    <th>${attribute.name}</th>
                                </#list>
                                </tr>
                                </tfoot>
                            </table>
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

<#include "../footer.ftl">
<#include "../control.ftl">

</div>
<#include "../scripts.ftl">
<script>

    $(function () {
        $("#example2").DataTable();
    });
</script>

</body>
</html>
