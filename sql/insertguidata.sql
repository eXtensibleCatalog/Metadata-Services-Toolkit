-- Insert default admin user
insert into users values(1,'admin','MST admin','0DPiKuNIrrVmD8IUCuw1hQxNqZc=','sranganathan@library.rochester.edu',1,'2008-10-20 00:00:00','2008-10-20 00:00:00',0);

-- Insert group for admin user
insert into users_to_groups values(1,1,1);

-- insert Email configuration values
insert into emailconfig (email_config_id, server_address, port_number, from_address, password) values(1,"mail.rochester.edu",25, 'xcsupport@library.rochester.edu', 'Migration1');