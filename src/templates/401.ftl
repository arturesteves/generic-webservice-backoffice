<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>500 Error</title>

<#include "./head.ftl">

</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">

<#include "./header.ftl">

<#include "./sidebar.ftl">

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1>
                500 Error Page
            </h1>
            <ol class="breadcrumb">
                <li><a href="#"><i class="fa fa-dashboard"></i> Home</a></li>
                <li><a href="#">Examples</a></li>
                <li class="active">500 error</li>
            </ol>
        </section>

        <!-- Main content -->
        <section class="content">

            <div class="error-page">
                <h2 class="headline text-red">500</h2>

                <div class="error-content">
                    <h3><i class="fa fa-warning text-red"></i> Oops! Access unauthorized.</h3>

                    <p>
                        You don't have authorization to access this resource!
                        Pelease log in <a href="/index">return to dashboard</a>
                    </p>

                </div>

            </div>

        </section>
        <!-- /.content -->
    </div>

<#include "./footer.ftl">

<#include "./control.ftl">
    <!-- Add the sidebar's background. This div must be placed
         immediately after the control sidebar -->
    <div class="control-sidebar-bg"></div>
</div>

<#include "./scripts.ftl">

</body>
</html>