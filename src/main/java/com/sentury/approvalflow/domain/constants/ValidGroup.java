package com.sentury.approvalflow.domain.constants;


import jakarta.validation.groups.Default;

public interface ValidGroup extends Default {

    interface Crud extends ValidGroup{
        interface Create extends Crud{

        }

        interface Update extends Crud{

        }

        interface Query extends Crud{

        }

        interface Delete extends Crud{

        }
    }
}
