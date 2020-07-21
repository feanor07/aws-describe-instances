# Description
This is a small project to illustrate `DescribeInstancesRequest` for `AWS EC2 instances`. The purpose is to illustrate 
`security-group-name` filter is not working for contemporary EC2 instances (ones within a VPC, not [EC2-Classic](
https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-classic-platform.html
)).

# Getting Started
Before running the application, do not forget to update the `application.properties` with corresponding from your AWS account.
If you do not have any EC2 instances available in the region you specified, ensure you have at least one before running this app.

In order to run the application you need to provide two command line arguments to be used `filterName` and `filterValue`.

# Testing
For testing purposes I launched a `t2.micro` instance within a security group named `Trial` and with a tag key and value of
`group: Trial`.

First execution of the application is performed with arguments "group-name" and "trial" in order to launch a `DescribeInstanceRequest` 
with a security group. The logger output contains: `Could not found any instance with filter key: group-name and filter value: Trial`

Second execution of the application is performed with arguments "tag:group" and "trial" in order to launch a `DescribeInstanceRequest`
with a tag key-value pair. The logger output contains `Found instance with id i-0bf8h17f8uf596f53 and security groups [Trial]`.

To conclude, even if the instance resides in a security group named `Trial`, the first execution of the application failed 
to return the instance in the result set.

This small testing illustrates that security group name search for contemporary EC2 instances (ones within a VPC) is not working!
