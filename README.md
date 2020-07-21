# Description
This is a small project to illustrate `DescribeInstancesRequest` for `AWS EC2 instances`. The purpose is to illustrate 
`security-group-name` filter is not working for contemporary EC2 instances (ones within a VPC, not [EC2-Classic](
https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ec2-classic-platform.html
)).

# Motivation
Sometime ago I ran into an issue 
while using [hazelcast-aws](https://github.com/hazelcast/hazelcast-aws). I tried to make use of `security-group-name` filter 
as indicated in the documentation, but the caching failed to work as expected within a cluster of EC2 instances within the
same security group. Specifically, cache update on a particular instance was not visible to other instances; hence all but 
one of the instances was returning stale data.

The root cause of the issue is due to the fact that `security-group-name` filter 
can only be used in a EC2-Classic instance as indicated at [API documentation on AWS](https://docs.aws.amazon.com/AWSEC2/latest/APIReference/API_DescribeInstances.html)
(Please search for `group-name` on the provided link).

There is already an open [ticket](https://github.com/hazelcast/hazelcast-aws/issues/94) targeting this issue on [hazelcast-aws](https://github.com/hazelcast/hazelcast-aws); 
yet the documentation is still lacking a warning not to use `security-group-name` in non EC2-Classic instances.

This application is only provided to demonstrate the issue mentioned.

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

