<!-- Left side column. contains the logo and sidebar -->
<aside class="main-sidebar">
    <!-- sidebar: style can be found in sidebar.less -->
    <section class="sidebar">
        <!-- Sidebar user panel -->
        <div class="user-panel">
            <div class="pull-left image">
                <img src="/dist/img/user2-160x160.jpg" class="img-circle" alt="User Image">
            </div>
            <div class="pull-left info">
                <p><#if userOnline??>${userOnline}</#if></p>
                <a href="#"><i class="fa fa-circle text-success"></i> Online</a>
            </div>
        </div>

        <!-- sidebar menu: : style can be found in sidebar.less -->
        <ul class="sidebar-menu">
            <li class="header">MAIN NAVIGATION</li>
            <li class="treeview active">
                <a href="#">
                    <i class="fa fa-server"></i> <span>Servers</span>
                    <span class="pull-right-container">
                  <i class="fa fa-angle-left pull-right"></i>
                </span>
                </a>
                <ul class="treeview-menu">
                    <li class="treeview">
                        <a href="/"><i class="fa fa-dashboard"></i>Dashboard</a>
                    </li>
                <!-- list server menus -->
                <#if servers??>
                    <#list servers as server>
                    <li class="treeview">
                        <a href="#">
                            <i class="fa fa-globe"></i>
                        ${server.name}
                            <#if server.entities??>
                                <span class="pull-right-container">
                                <i class="fa fa-angle-left pull-right"></i>
                            </span>
                            </#if>
                        </a>
                        <ul class="treeview-menu" style="display: block;">
                            <li class="treeview">
                                <a href="/server/${server.name}"><i class="fa fa-dashboard"></i>Dashboard</a>
                            </li>
                            <#if server.entities??>
                                <#list server.entities as entity>
                                <li class="treeview">
                                    <a href="/server/${server.name?lower_case}/entity/${entity.name?lower_case}">
                                        <i class="fa fa-cube"></i> ${entity.name}
                                    </a>
                                </li>
                                </#list>
                            </#if>
                        </ul>
                    </li>
                    </#list>
                </#if>
                </ul>
            </li>
        </ul>
    </section>
    <!-- /.sidebar -->
</aside>
<script>
    $("ul li").on("click", function() {
        $("li").removeClass("active");
        $(this).addClass("active");
    });

</script>