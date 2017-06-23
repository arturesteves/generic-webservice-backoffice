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
                <li><a href="/server/${server}"><i class="fa fa-globe"></i> ${server}</a></li>
                <li class="active"><i class="fa fa-cube"></i> ${entity}</li>
            </ol>
        </section>

        <div class="modal modal-danger fade" id="modal-danger" style="display: none;">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">×</span></button>
                        <h4 class="modal-title">Remove ${entity}</h4>
                    </div>
                    <div class="modal-body">
                        <p>Are you sure you want to remove the ${entity}</p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-outline pull-left" data-dismiss="modal">Close</button>
                        <button onclick="remove(window.selected, window.superEntity)" ) type="button" class="btn btn-outline">Remove
                        </button>
                    </div>
                </div>
                <!-- /.modal-content -->
            </div>
            <!-- /.modal-dialog -->
        </div>

        <!-- Main content -->
        <section class="content">
            <div class="row">
                <div class="col-xs-12">

                    <div class="box">
                        <div class="box-header">
                            <h3 class="box-title">${entity}</h3>
                        </div>
                        <!-- /.box-header -->
                        <div class="box-body">

                            <table id="example2" class="table table-bordered table-striped dt-responsive">
                                <thead>
                                <tr>
                                    <th>id</th>
                                <#list attributes as attribute>
                                    <th>${(attribute.displayName??)?then(attribute.displayName, attribute.name)}</th>
                                </#list>
                                    <th>type</th>
                                    <th>action</th>
                                </tr>
                                </thead>
                                <tbody>
                                <#list instances as instance>
                                <tr>
                                    <td>${instance.id}</td>
                                    <#list attributes as attribute>
                                        <#if instance[attribute.name]?? >
                                            <#if !(attribute.type == "association") && !instance[attribute.name]?is_string>
                                            <td>${instance[attribute.name]?c}</td>
                                            <#elseif !(attribute.type == "association")>
                                            <td>${instance[attribute.name]}</td>
                                            <#elseif attribute.type == "association" && attribute.maxOccurs == "*">
                                            <td><#list instance[attribute.name] as instance>
                                                <small class="label bg-blue">${instance}</small><#sep> </#sep>
                                            </#list></td>
                                            <#else>
                                            <td><small class="label bg-blue">${(instance[attribute.name]??)?then(instance[attribute.name], "")}</small></td>
                                            </#if>
                                        <#else>
                                            <td>null</td>
                                        </#if>
                                    </#list>
                                    <#if instance.superEntity??>
                                    <td>${instance.superEntity}</td>

                                    <td><small class="label bg-yellow"  class="btn-link"><a style="color: white" href="${instance.superEntity?lower_case}/instance/${instance.id}">Edit</a></small> |
                                        <small class="label bg-red" class="btn-link" data-toggle="modal" data-target="#modal-danger"
                                                onclick="window.selected = ${instance.id}; window.superEntity = '${instance.superEntity?lower_case}'">Remove
                                        </small>
                                    </td>
                                    </else>
                                        <td>${entity}</td>
                                    <td><small class="label bg-yellow"  class="btn-link"><a style="color: white" href="${entity?lower_case}/instance/${instance.id}">Edit</a></small> |
                                        <small class="label bg-red" class="btn-link" data-toggle="modal" data-target="#modal-danger"
                                               onclick="window.selected = ${instance.id}; window.superEntity = '${entity?lower_case}'">Remove
                                        </small>
                                    </td>

                                </#if>


                                </tr>
                                </#list>
                                </tbody>
                                <tfoot>
                                <tr>
                                    <th>id</th>
                                <#list attributes as attribute>
                                    <th>${(attribute.displayName??)?then(attribute.displayName, attribute.name)}</th>
                                </#list>
                                    <th>type</th>
                                    <th>action</th>
                                </tr>
                                </tfoot>
                            </table>

                        </div>
                        <!-- /.box-body -->
                        <div class="box-footer">
                            <a class="btn  btn-default" href="${entity}/instance/add"">Add new</a>
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

<#include "../footer.ftl">
<#include "../control.ftl">

</div>
<#include "../scripts.ftl">
<script>

    $(function () {
        var table = $("#example2").DataTable({

        });
    });



    //    $('#modal-danger').modal()
    function remove(selectedId, entity) {

        $.ajax({
            url: "${host}/api/entity/" + entity + "/instance/" + selectedId,
            type: 'DELETE',
            success: function (result) {
                location.reload();
            }
        });
    }

</script>

</body>
</html>
